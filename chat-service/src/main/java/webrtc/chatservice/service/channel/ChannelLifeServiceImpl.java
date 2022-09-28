package webrtc.chatservice.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.*;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;
import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.exception.ChannelException.*;
import webrtc.chatservice.exception.HashTagException.NotExistHashTagException;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.channel.ChannelCrudRepository;
import webrtc.chatservice.repository.channel.ChannelListRepository;
import webrtc.chatservice.repository.channel.ChannelRedisRepository;
import webrtc.chatservice.repository.hashtag.ChannelHashTagRepository;
import webrtc.chatservice.repository.hashtag.HashTagRepository;
import webrtc.chatservice.repository.users.ChannelUserRepository;
import webrtc.chatservice.repository.users.UsersRepository;
import webrtc.chatservice.service.chat.factory.ChattingMessageFactory;
import webrtc.chatservice.service.rabbit.RabbitPublish;
import java.util.List;
import java.util.Optional;

import static webrtc.chatservice.enums.ClientMessageType.CREATE;


@RequiredArgsConstructor
@Service
public class ChannelLifeServiceImpl implements ChannelLifeService {

    private final ChannelCrudRepository channelCrudRepository;
    private final ChannelHashTagRepository channelHashTagRepository;
    private final ChannelRedisRepository channelRedisRepository;
    private final ChannelUserRepository channelUserRepository;
    private final UsersRepository usersRepository;
    private final HashTagRepository hashTagRepository;
    private final RabbitPublish rabbitPublish;
    private final ChattingMessageFactory chattingMessageFactory;


    // 30분당 100포인트
    private final Long pointUnit = 100L;
    private final Long channelCreatePoint = 2L;
    private final Long channelExtensionMinute = 30L;
    private final HttpApiController httpApiController;

    /**
     * 비즈니스 로직 - 채널을 생성
     * 비즈니스 로직 순서 :
     * 1) 같은 이름을 가진 채널이 기존에 존재하는지 확인 -> 있으면 Exception 발생
     * 2) 채널과 해시태그 관계 설정 - 같은 이름을 가진 해시태그가 이미 존재하면, 해당 해시태그 사용 없으면 생성
     * 3) 회원의 포인트 감소 시킴 -> 외부 회원 서비스와 통신 -> 포인트가 부족하면 Exception 발생
     * 4) 채널과 유저 관계 설정
     * 5) 기본 채팅 로그 생성 및 채널과 관계 설정
     * 6) RabbitMQ로 채널 생성 로그 전송
     */
    @Transactional
    public Channel createChannel(CreateChannelRequest request, String email) {
        Channel channel = createChannelIfNotExist(request);
        channelCrudRepository.save(channel);

        request.getHashTags().forEach(tagName -> {
            HashTag hashTag = findHashTag(tagName);
            hashTagRepository.save(hashTag);
            createChannelHashTag(channel, hashTag);
        });

        channelRedisRepository.createChannel(channel);
        Users user = pointDecreaseAndReturnUser(email);

        // 채널유저 생성
        createChannelUser(user, channel);

        // 채팅방 생성 로그
        createChatLog(channel, user);

        channelCrudRepository.save(channel);

        // rabbitMQ 메시지 전송
//        sendRabbitMessage(channel, user);
        return channel;
    }

    /*
     * 비즈니스 로직 - 채널 삭제
     * 채널 삭제는 채널의 수명이 끝나면 이벤트가 발생하여 자동으로 실행됨
     * 비즈니스 로직 순서 :
     * 1) 삭제할 채널 확인
     * 2) DB에서 채널 삭제
     * 3) Redis에서 채널 삭제
     */
    @Transactional
    public void deleteChannel(String channelId) {
        Channel channel = channelCrudRepository.findById(channelId).orElseThrow(NotExistChannelException::new);
        channelCrudRepository.delete(channel);
        channelRedisRepository.delete(channelId);
    }

    /*
     * 비즈니스 로직 - 채널 수명 연장 (추가할 것 : 채널의 남은 수명이 1분 미만일 경우 증가를 못하게 시켜야 할듯 싶음 )
     * 비즈니스 로직 순서 :
     * 1) 수명을 연장할 채널 확인
     * 2) 회부 회원 서비스와 통신하여 회원의 포인트 감소 -> 포인트 부족 or 존재하지 않는 회원의 요청시 Exception 발생
     * 3) 포인트 사용 성공 시 채널의 수명 증가
     */
    @Transactional
    public void extensionChannelTTL(String channelId, String userEmail, Long requestTTL) {
        Channel channel = channelCrudRepository.findById(channelId).orElseThrow(NotExistChannelException::new);
        httpApiController.postDecreaseUserPoint(userEmail, requestTTL * pointUnit, userEmail + " 님이 채널 연장에 포인트를 사용했습니다.");
        channelRedisRepository.extensionChannelTTL(channel, requestTTL * channelExtensionMinute * 60L);
    }


    /*
     * 비즈니스 로직 - 특정 채널 이름으로 DB 검색
     * 비즈니스 로직 순서 :
     * 1) 채널 이름으로 검색 -> 만약 이미 존재한다면 Exception 발생
     * 2) 채널의 이름과 Type ( 문자, 음성 )을 이용해 새로운 채널 생성
     */
    private Channel createChannelIfNotExist(CreateChannelRequest request) {

        channelCrudRepository.findByChannelName(request.getChannelName())
                .ifPresent(channel -> { throw new AlreadyExistChannelException(); });

        return new Channel(request.getChannelName(), request.getChannelType());
    }

    /*
     * 비즈니스 로직 - 채널 생성 시 외부 회원 서비스와 통신하여 포인트 감소
     * 비즈니스 로직 순서 :
     * 1) email을 이용해 회원 서비스로 요청 -> 포인트 부족 or 회원 null시 Exception 발생
     * 2) Response에 들어있는 회원 정보를 DB에 저장
     */
    private Users pointDecreaseAndReturnUser(String email) {

        httpApiController.postDecreaseUserPoint(email, channelCreatePoint * pointUnit, email + " 님이 채널 생성에 포인트를 사용했습니다.");
        Users user = usersRepository.findByEmail(email)
                .orElse(httpApiController.postFindUserByEmail(email));
        usersRepository.save(user);
        return user;

    }

    /*
     * 비즈니스 로직 - 해시태그 검색 후 반환
     * 비즈니스 로직 순서 :
     * 1) 해시 태그 이름으로 검색
     * 2) DB에 해시태그가 없으면 새로 생성
     */
    private HashTag findHashTag(String tagName) {
        return hashTagRepository.findByTagName(tagName)
                .orElse(new HashTag(tagName));
    }

    /*
     * 비즈니스 로직 - 회원과 채널간의 관계 생성 ( 채널 생성용, 이미 생성된 채널에 회원이 입장하는 로직은 따로 존재 )
     * 비즈니스 로직 순서 :
     * 1) ChannelUser 생성 후 저장
     */
    private void createChannelUser(Users user, Channel channel) {
        ChannelUser channelUser = new ChannelUser(user, channel);
        channelUserRepository.save(channelUser);
    }

    /*
     * 비즈니스 로직 - 해시태그와 채널간의 관계 생성
     * 비즈니스 로직 순서 :
     * 1) ChannelHashTag 생성 후 저장
     */
    private void createChannelHashTag(Channel channel, HashTag hashTag) {
        ChannelHashTag channelHashTag = new ChannelHashTag(channel, hashTag);
        channelHashTagRepository.save(channelHashTag);
    }

    /*
     * 비즈니스 로직 - 채널 생성시 넣은 채팅 로그 생성
     * 비즈니스 로직 순서 :
     * 1) 채팅 로그 생성 후 저장
     */
    private void createChatLog(Channel channel, Users user) {
        ChatLog chatLog = new ChatLog(CREATE, "[알림] " + user.getNickname() + "님이 채팅방을 생성했습니다.", user.getNickname(), "NOTICE");
        channel.addChatLog(chatLog);
    }

    /*
     * 비즈니스 로직 - RabbitMq로 채널 생성 메시지 전송
     * 비즈니스 로직 순서 :
     * 1) 채팅 메시지 생성 후 전송
     */
    private void sendRabbitMessage(Channel channel, Users user) {
        ChattingMessage serverMessage = chattingMessageFactory.createMessage(channel.getId(), CREATE, user.getNickname(), "[알림] " + user.getNickname() + "님이 채팅방을 생성했습니다.", 1L, List.of(user), 1L, user.getEmail());
        rabbitPublish.publishMessage(serverMessage, CREATE);
    }
}
