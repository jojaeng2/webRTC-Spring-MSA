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
import webrtc.v1.chat.repository.ChatLogRedisRepositoryImpl;
import webrtc.v1.channel.exception.ChannelException.AlreadyExistChannelException;
import webrtc.v1.channel.exception.ChannelException.NotExistChannelException;
import webrtc.v1.chat.service.ChatLogService;
import webrtc.v1.point.exception.PointException.InsufficientPointException;
import webrtc.v1.point.repository.PointRepository;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.channel.repository.ChannelCrudRepository;
import webrtc.v1.channel.repository.ChannelRedisRepository;
import webrtc.v1.hashtag.entity.HashTag;
import webrtc.v1.channel.repository.ChannelHashTagRepository;
import webrtc.v1.hashtag.repository.HashTagRepository;
import webrtc.v1.point.entity.Point;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.repository.ChannelUserRepository;
import webrtc.v1.user.repository.UsersRepository;
import webrtc.v1.voice.repository.VoiceRoomRepository;

import java.util.UUID;


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
    private final ChatLogRedisRepositoryImpl chatLogRedisRepositoryImpl;


    private final long pointUnit = 1L;
    private final long channelCreatePoint = 2L;
    private final long channelExtensionMinute = 30L;

    @Transactional
    public Channel create(CreateChannelRequest request, String userId) {
        Channel channel = createChannelIfNotExist(request);
        request.getHashTags().forEach(tagName -> {
            createChannelHashTag(channel, tagName);
        });
        Users user = userPointDecrease(userId);
        createChannelUser(user, channel);
        createChatLog(channel, user);
        channelCrudRepository.save(channel);
        return channel;
    }

    public void delete(String channelId) {
        Channel channel = findChannelById(channelId);
        channelCrudRepository.delete(channel);
        channelRedisRepository.delete(channelId);
        voiceRoomRepository.delete(channelId);
        chatLogRedisRepositoryImpl.delete(channelId);
    }

    @Transactional
    public Channel extension(String channelId, String userId, Long requestTTL) {
        Channel channel = findChannelById(channelId);
        Users user = findUserById(userId);
        int sum = getPointSumByUser(user);
        // 포인트 부족
        if (sum < requestTTL * pointUnit) throw new InsufficientPointException();
        Point point = Point.extensionChannelTTL(user.getEmail(), requestTTL);
        user.addPoint(point);
        channelRedisRepository.extensionTtl(channel, requestTTL * channelExtensionMinute * 60L);
        return channel;
    }


    private Channel createChannelIfNotExist(CreateChannelRequest request) {
        channelCrudRepository
                .findByChannelName(request.getChannelName())
                .ifPresent(
                        channel -> {
                            throw new AlreadyExistChannelException();
                        }
                );
        Channel channel = Channel.builder()
                .channelName(request.getChannelName())
                .channelType(request.getChannelType())
                .build();
        channelCrudRepository.save(channel);
        channelRedisRepository.save(channel);
        return channel;
    }

    private Users userPointDecrease(String userId) {

        Users user = findUserById(userId);

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



    private HashTag hashTagBuilder(String name) {
        HashTag hashTag = HashTag.builder()
                .name(name)
                .build();
        hashTagRepository.save(hashTag);
        return hashTag;
    }

    private void createChannelUser(Users user, Channel channel) {
        ChannelUser channelUser = ChannelUser.builder()
                .user(user)
                .channel(channel)
                .build();
        channel.enterChannelUser(channelUser);
        channelUserRepository.save(channelUser);
    }


    private void createChatLog(Channel channel, Users user) {
        ChatLog chatLog = ChatLog.createChannelLog(user);
        chatLogRedisRepositoryImpl.save(channel.getId(), chatLog);
        chatLogRedisRepositoryImpl.addLastIndex(channel.getId());
        channel.addChatLog(chatLog);
    }

    private void createChannelHashTag(Channel channel, String tagName) {
        HashTag hashTag = findHashTag(tagName);
        ChannelHashTag channelHashTag = ChannelHashTag.builder()
                .channel(channel)
                .hashTag(hashTag)
                .build();
        channel.addChannelHashTag(channelHashTag);
        hashTag.addChannelHashTag(channelHashTag);
        channelHashTagRepository.save(channelHashTag);
    }

    private Users findUserById(String id) {
        return usersRepository.findById(id)
                .orElseThrow(NotExistUserException::new);
    }

    private Channel findChannelById(String id) {
        return channelCrudRepository.findById(id)
                .orElseThrow(NotExistChannelException::new);
    }

    private HashTag findHashTag(String name) {
        return hashTagRepository.findByName(name)
                .orElse(hashTagBuilder(name));
    }

    private int getPointSumByUser(Users user) {
        return pointRepository.findByUser(user).stream()
                .map(Point::getAmount)
                .reduce(0, Integer::sum);
    }
}
