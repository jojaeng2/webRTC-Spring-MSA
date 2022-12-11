package webrtc.v1.channel.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.entity.ChannelHashTag;
import webrtc.v1.channel.entity.ChannelUser;
import webrtc.v1.chat.entity.ChatLog;
import webrtc.v1.channel.dto.ChannelDto.CreateChannelRequest;
import webrtc.v1.chat.repository.ChatLogRedisRepository;
import webrtc.v1.channel.enums.ChannelType;
import webrtc.v1.channel.exception.ChannelException.AlreadyExistChannelException;
import webrtc.v1.channel.exception.ChannelException.NotExistChannelException;
import webrtc.v1.point.exception.PointException.InsufficientPointException;
import webrtc.v1.point.repository.PointRepository;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.channel.repository.ChannelCrudRepository;
import webrtc.v1.channel.repository.ChannelRedisRepository;
import webrtc.v1.hashtag.entity.HashTag;
import webrtc.v1.hashtag.repository.ChannelHashTagRepository;
import webrtc.v1.hashtag.repository.HashTagRepository;
import webrtc.v1.point.entity.Point;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.repository.ChannelUserRepository;
import webrtc.v1.user.repository.UsersRepository;
import webrtc.v1.voice.repository.VoiceRoomRepository;

import java.util.UUID;

import static webrtc.v1.channel.enums.ChannelType.VOIP;


@Slf4j
@RequiredArgsConstructor
@Service
public class ChannelLifeServiceImpl implements ChannelLifeService {

    private final ChannelCrudRepository channelCrudRepository;
    private final ChannelHashTagRepository channelHashTagRepository;
    private final ChannelRedisRepository channelRedisRepository;
    private final ChannelUserRepository channelUserRepository;
    private final UsersRepository usersRepository;
    private final HashTagRepository hashTagRepository;
    private final VoiceRoomRepository voiceRoomRepository;
    private final PointRepository pointRepository;
    private final ChatLogRedisRepository chatLogRedisRepository;


    // 30분당 100포인트
    private final long pointUnit = 1L;
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
    public Channel create(CreateChannelRequest request, UUID userId) {

        Channel channel = createChannelIfNotExist(request);

        request.getHashTags().forEach(tagName -> {
            createChannelHashTag(channel, tagName);
        });
        Users user = userPointDecrease(userId);

        // 채널유저 생성
        createChannelUser(user, channel);

        // 채팅방 생성 로그
        ChatLog chatLog = ChatLog.createChannelLog(user);
        chatLogRedisRepository.addLastIndex(channel.getId());
        channel.addChatLog(chatLog);

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
    public void delete(String channelId) {
        Channel channel = channelCrudRepository.findById(channelId)
                .orElseThrow(NotExistChannelException::new);
        channelCrudRepository.delete(channel);
        channelRedisRepository.delete(channelId);
        voiceRoomRepository.delete(channelId);
        chatLogRedisRepository.delete(channelId);
    }

    /*
     * 비즈니스 로직 - 채널 수명 연장 (추가할 것 : 채널의 남은 수명이 1분 미만일 경우 증가를 못하게 시켜야 할듯 싶음 )
     * 비즈니스 로직 순서 :
     * 1) 수명을 연장할 채널 확인
     * 2) 회부 회원 서비스와 통신하여 회원의 포인트 감소 -> 포인트 부족 or 존재하지 않는 회원의 요청시 Exception 발생
     * 3) 포인트 사용 성공 시 채널의 수명 증가
     */
    @Transactional
    public Channel extension(String channelId, UUID userId, Long requestTTL) {
        Channel channel = channelCrudRepository.findById(channelId)
                .orElseThrow(NotExistChannelException::new);
        Users user = usersRepository.findById(userId)
                .orElseThrow(NotExistUserException::new);

        int sum = pointRepository.findByUser(user).stream()
                .map(Point::getAmount)
                .reduce(0, Integer::sum);

        // 포인트 부족
        if (sum < requestTTL * pointUnit) throw new InsufficientPointException();

        Point point = Point.extensionChannelTTL(user.getEmail(), requestTTL);
        user.addPoint(point);

        channelRedisRepository.extensionTtl(channel, requestTTL * channelExtensionMinute * 60L);
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
    private Users userPointDecrease(UUID userId) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(NotExistUserException::new);

        int sum = pointRepository.findByUser(user).stream()
                .map(Point::getAmount)
                .reduce(0, Integer::sum);

        // 포인트 부족
        if (sum < channelCreatePoint * pointUnit) throw new InsufficientPointException();

        Point point = Point.createChannel(user.getEmail());
        user.addPoint(point);
        usersRepository.save(user);
        return user;
    }

    /*
     * 비즈니스 로직 - 해시태그 검색 후 반환
     * 비즈니스 로직 순서 :
     * 1) 해시 태그 이름으로 검색
     * 2) DB에 해시태그가 없으면 새로 생성
     */
    private HashTag findHashTag(String name) {
        return hashTagRepository.findByName(name)
                .orElse(hashTagBuilder(name));
    }

    private HashTag hashTagBuilder(String name) {
        HashTag hashTag = HashTag.builder()
                .name(name)
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
}
