package webrtc.v1.channel.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.channel.dto.ChannelDto.CreateChannelDto;
import webrtc.v1.channel.dto.ChannelDto.CreateChannelRequest;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.entity.ChannelHashTag;
import webrtc.v1.channel.entity.ChannelUser;
import webrtc.v1.channel.exception.ChannelException.AlreadyExistChannelException;
import webrtc.v1.channel.exception.ChannelException.NotExistChannelException;
import webrtc.v1.channel.repository.ChannelCrudRepository;
import webrtc.v1.channel.repository.ChannelHashTagRepository;
import webrtc.v1.channel.repository.ChannelRedisRepository;
import webrtc.v1.chat.entity.ChatLog;
import webrtc.v1.chat.repository.ChatLogRedisRepositoryImpl;
import webrtc.v1.chat.service.ChattingService;
import webrtc.v1.hashtag.entity.HashTag;
import webrtc.v1.hashtag.repository.HashTagRepository;
import webrtc.v1.point.entity.Point;
import webrtc.v1.point.exception.PointException.InsufficientPointException;
import webrtc.v1.point.repository.PointRepository;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.channel.repository.ChannelUserRepository;
import webrtc.v1.user.repository.UsersRepository;
import webrtc.v1.voice.repository.VoiceRoomRepository;

import static webrtc.v1.point.enums.PointUnit.CREATE_CHANNEL;
import static webrtc.v1.point.enums.PointUnit.EXTENSION_CHANNEL;


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
  private final ChattingService chattingService;
  private final ChatLogRedisRepositoryImpl chatLogRedisRepositoryImpl;


  @Transactional
  public Channel create(CreateChannelDto request) {
    Channel channel = createChannelIfNotExist(request);
    request.getHashTags().forEach(tagName -> {
      createChannelHashTag(channel, tagName);
    });
    Users user = userPointDecrease(request.getUserId());
    createChannelUser(user, channel);
    createChatLog(channel, user);
    channelCrudRepository.save(channel);
    return channel;
  }

  @Transactional
  public void delete(String channelId) {
    Channel channel = findChannelById(channelId);
    deleteChannel(channel);
    chattingService.closeChannel(channel);
  }

  @Transactional
  public Channel extension(String channelId, String userId, Long requestTTL) {
    Channel channel = findChannelById(channelId);
    Users user = findUserById(userId);
    int sum = getPointSumByUser(user);
    // 포인트 부족
    if (sum < requestTTL) {
      throw new InsufficientPointException();
    }
    Point point = Point.extensionChannelTTL(user.getEmail(), requestTTL);
    user.addPoint(point);
    channelRedisRepository.extensionTtl(channel, requestTTL * EXTENSION_CHANNEL.getUnit());
    return channel;
  }


  private void deleteChannel(Channel channel) {
    channelCrudRepository.delete(channel);
    channelRedisRepository.delete(channel.getId());
    voiceRoomRepository.delete(channel.getId());
    chatLogRedisRepositoryImpl.delete(channel.getId());
  }

  private Channel createChannelIfNotExist(CreateChannelDto request) {
    isValidChannelName(request.getChannelName());
    Channel channel = Channel.builder()
        .channelName(request.getChannelName())
        .channelType(request.getType())
        .build();
    saveChannel(channel);
    return channel;
  }

  private void saveChannel(Channel channel) {
    channelCrudRepository.save(channel);
    channelRedisRepository.save(channel);
  }

  private void isValidChannelName(String name) {
    channelCrudRepository
        .findByChannelName(name)
        .ifPresent(it -> {
          throw new AlreadyExistChannelException();
        });
  }

  private Users userPointDecrease(String userId) {
    Users user = findUserById(userId);
    int sumOfPoint = getPointSumByUser(user);
    if (sumOfPoint >= CREATE_CHANNEL.getUnit()) {
      Point point = Point.createChannel(user.getEmail());
      user.addPoint(point);
      usersRepository.save(user);
      return user;
    }
    throw new InsufficientPointException();
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
