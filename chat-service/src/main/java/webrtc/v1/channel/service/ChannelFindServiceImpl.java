package webrtc.v1.channel.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.channel.dto.ChannelDto.FindChannelByHashTagDto;
import webrtc.v1.channel.dto.ChannelDto.FindChannelDto;
import webrtc.v1.channel.dto.ChannelDto.FindMyChannelDto;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.exception.ChannelException.NotExistChannelException;
import webrtc.v1.channel.repository.ChannelCrudRepository;
import webrtc.v1.channel.repository.ChannelListRepository;
import webrtc.v1.hashtag.entity.HashTag;
import webrtc.v1.hashtag.exception.HashTagException.NotExistHashTagException;
import webrtc.v1.hashtag.repository.HashTagRepository;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.user.repository.UsersRepository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static webrtc.v1.channel.enums.OrderType.ASC;
import static webrtc.v1.channel.enums.OrderType.DESC;

@Service
@RequiredArgsConstructor
public class ChannelFindServiceImpl implements ChannelFindService {

  private final ChannelListRepository channelListRepository;
  private final ChannelCrudRepository channelCrudRepository;
  private final UsersRepository usersRepository;
  private final HashTagRepository hashTagRepository;
  private final ChannelInfoInjectService channelInfoInjectService;
  private final Map<String, String> orderMap = new HashMap<>();


  @Transactional(readOnly = true)
  public Channel findById(String id) {
    Channel channel = channelCrudRepository.findById(id)
        .orElseThrow(NotExistChannelException::new);
    return channelInfoInjectService.setTtl(channel);
  }

  @Transactional(readOnly = true)
  public List<Channel> findAnyChannel(FindChannelDto request) {
    return channelListRepository.findAnyChannels(request.getIdx(), findOrderType(request.getType()))
        .stream()
        .map(channelInfoInjectService::setTtl)
        .collect(toList());
  }

  @Transactional(readOnly = true)
  public List<Channel> findMyChannel(FindMyChannelDto request) {
    final Users user = findUserById(request.getUserId());
    return channelListRepository.findMyChannels(user.getId(), request.getIdx(),
            findOrderType(request.getType()))
        .stream()
        .map(channelInfoInjectService::setTtl)
        .collect(toList());
  }

  @Transactional(readOnly = true)
  public List<Channel> findByHashName(FindChannelByHashTagDto request) {
    HashTag hashTag = findHashTagByName(request.getTagName());
    return channelListRepository.findChannelsByHashName(hashTag, request.getIdx(), findOrderType(request.getType()))
        .stream()
        .map(channelInfoInjectService::setTtl)
        .collect(toList());
  }

  @Transactional(readOnly = true)
  public List<Channel> findChannelsRecentlyTalk(FindChannelDto request) {
    return channelListRepository.findChannelsRecentlyTalk(request.getIdx(),
            findOrderType(request.getType()))
        .stream()
        .map(channelInfoInjectService::setTtl)
        .collect(toList());
  }

  private String findOrderType(String type) {
    if (Objects.equals(type, "partiDESC")) {
      return DESC.getType();
    }
    return ASC.getType();
  }

  private HashTag findHashTagByName(String name) {
    return hashTagRepository.findByName(name)
        .orElseThrow(NotExistHashTagException::new);
  }

  private Users findUserById(String id) {
    return usersRepository.findById(id)
        .orElseThrow(NotExistUserException::new);
  }
}
