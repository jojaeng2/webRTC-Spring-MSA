package webrtc.v1.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.domain.*;
import webrtc.v1.dto.ChannelDto.CreateChannelRequest;
import webrtc.v1.enums.ChannelType;
import webrtc.v1.exception.ChannelException.AlreadyExistChannelException;
import webrtc.v1.exception.ChannelException.NotExistChannelException;
import webrtc.v1.exception.PointException.InsufficientPointException;
import webrtc.v1.exception.UserException.NotExistUserException;
import webrtc.v1.repository.channel.ChannelCrudRepository;
import webrtc.v1.repository.channel.ChannelRedisRepository;
import webrtc.v1.repository.hashtag.ChannelHashTagRepository;
import webrtc.v1.repository.hashtag.HashTagRepository;
import webrtc.v1.repository.users.ChannelUserRepository;
import webrtc.v1.repository.users.UsersRepository;

import static webrtc.v1.enums.ClientMessageType.CREATE;


@RequiredArgsConstructor
@Service
public class ChannelLifeServiceImpl implements ChannelLifeService {

    private final ChannelCrudRepository channelCrudRepository;
    private final ChannelHashTagRepository channelHashTagRepository;
    private final ChannelRedisRepository channelRedisRepository;
    private final ChannelUserRepository channelUserRepository;
    private final UsersRepository usersRepository;
    private final HashTagRepository hashTagRepository;


    // 30분당 100포인트
    private final long pointUnit = 100L;
    private final long channelCreatePoint = 2L;
    private final long channelExtensionMinute = 30L;

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
        request.getHashTags().forEach(tagName -> {
            createChannelHashTag(channel, tagName);
        });

        Users user = userPointDecrease(email);

        // 채널유저 생성
        createChannelUser(user, channel);

        // 채팅방 생성 로그
        createChatLog(channel, user);
        channelCrudRepository.save(channel);
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
        Channel channel = channelCrudRepository.findById(channelId)
                .orElseThrow(NotExistChannelException::new);
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
    public Channel extensionChannelTTL(String channelId, String userEmail, Long requestTTL) {
        Channel channel = channelCrudRepository.findById(channelId)
                .orElseThrow(NotExistChannelException::new);
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(NotExistUserException::new);

        int sum = user.sumOfPoint();

        // 포인트 부족
        if (sum < requestTTL * pointUnit) throw new InsufficientPointException();

        Point point = Point.builder()
                .message(userEmail + " 님이 채널 연장에 포인트를 사용했습니다.")
                .amount(-(int) (requestTTL * pointUnit))
                .build();
        user.addPoint(point);

        channelRedisRepository.extensionChannelTTL(channel, requestTTL * channelExtensionMinute * 60L);
        return channel;
    }


    /*
     * 비즈니스 로직 - 특정 채널 이름으로 DB 검색
     * 비즈니스 로직 순서 :
     * 1) 채널 이름으로 검색 -> 만약 이미 존재한다면 Exception 발생
     * 2) 채널의 이름과 Type ( 문자, 음성 )을 이용해 새로운 채널 생성
     */
    private Channel createChannelIfNotExist(CreateChannelRequest request) {
        channelCrudRepository
                .findByChannelName(request.getChannelName())
                .ifPresent(
                        channel -> {
                            throw new AlreadyExistChannelException();
                        }
                );
        Channel channel = channelBuilder(request.getChannelName(), request.getChannelType());
        channelCrudRepository.save(channel);
        channelRedisRepository.save(channel);
        return channel;
    }

    private Channel channelBuilder(String name, ChannelType type) {
        return Channel.builder()
                .channelName(name)
                .channelType(type)
                .build();
    }

    /*
     * 비즈니스 로직 - 채널 생성 시 외부 회원 서비스와 통신하여 포인트 감소
     * 비즈니스 로직 순서 :
     * 1) email을 이용해 회원 서비스로 요청 -> 포인트 부족 or 회원 null시 Exception 발생
     * 2) Response에 들어있는 회원 정보를 DB에 저장
     */
    private Users userPointDecrease(String email) {

        Users user = usersRepository.findByEmail(email)
                .orElseThrow(NotExistUserException::new);

        int sum = user.sumOfPoint();

        // 포인트 부족
        if (sum < channelCreatePoint * pointUnit) throw new InsufficientPointException();

        Point point = pointBuilder(email);
        user.addPoint(point);
        usersRepository.save(user);
        return user;
    }

    private Point pointBuilder(String email) {
        return Point.builder()
                .message(email + " 님이 채널 생성에 포인트를 사용했습니다.")
                .amount(-(int) (channelCreatePoint * pointUnit))
                .build();
    }

    /*
     * 비즈니스 로직 - 해시태그 검색 후 반환
     * 비즈니스 로직 순서 :
     * 1) 해시 태그 이름으로 검색
     * 2) DB에 해시태그가 없으면 새로 생성
     */
    private HashTag findHashTag(String name) {
        return hashTagRepository.findByTagName(name)
                .orElse(hashTagBuilder(name));
    }

    private HashTag hashTagBuilder(String name) {
        HashTag hashTag = HashTag.builder()
                .tagName(name)
                .build();
        hashTagRepository.save(hashTag);
        return hashTag;
    }

    /*
     * 비즈니스 로직 - 회원과 채널간의 관계 생성 ( 채널 생성용, 이미 생성된 채널에 회원이 입장하는 로직은 따로 존재 )
     * 비즈니스 로직 순서 :
     * 1) ChannelUser 생성 후 저장
     */
    private void createChannelUser(Users user, Channel channel) {
        ChannelUser channelUser = channelUserBuilder(user, channel);
        channelUserRepository.save(channelUser);
    }

    private ChannelUser channelUserBuilder(Users user, Channel channel) {
        ChannelUser channelUser = ChannelUser.builder()
                .user(user)
                .channel(channel)
                .build();
        channel.enterChannelUser(channelUser);
        return channelUser;
    }

    /*
     * 비즈니스 로직 - 해시태그와 채널간의 관계 생성
     * 비즈니스 로직 순서 :
     * 1) ChannelHashTag 생성 후 저장
     */
    private void createChannelHashTag(Channel channel, String tagName) {
        HashTag hashTag = findHashTag(tagName);
        ChannelHashTag channelHashTag = channelHashTagBuilder(channel, hashTag);
        channelHashTagRepository.save(channelHashTag);
    }

    private ChannelHashTag channelHashTagBuilder(Channel channel, HashTag hashTag) {
        ChannelHashTag channelHashTag = ChannelHashTag.builder()
                .channel(channel)
                .hashTag(hashTag)
                .build();
        channel.addChannelHashTag(channelHashTag);
        hashTag.addChannelHashTag(channelHashTag);
        return channelHashTag;
    }

    /*
     * 비즈니스 로직 - 채널 생성시 넣은 채팅 로그 생성
     * 비즈니스 로직 순서 :
     * 1) 채팅 로그 생성 후 저장
     */
    private void createChatLog(Channel channel, Users user) {
        ChatLog chatLog = ChatLog.builder()
                .type(CREATE)
                .message("[알림] " + user.getNickname() + "님이 채팅방을 생성했습니다.")
                .senderNickname(user.getNickname())
                .senderEmail("NOTICE")
                .build();
        channel.addChatLog(chatLog);
    }
}