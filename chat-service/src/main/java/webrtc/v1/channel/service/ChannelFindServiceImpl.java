package webrtc.v1.channel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @PostConstruct
    private void createMapping() {
        orderMap.put("partiASC", ASC.getType());
        orderMap.put("partiDESC", DESC.getType());
    }

    @Transactional(readOnly = true)
    public Channel findById(String id) {
        Channel channel = channelCrudRepository.findById(id)
                .orElseThrow(NotExistChannelException::new);
        return channelInfoInjectService.setTtl(channel);
    }

    @Transactional(readOnly = true)
    public List<Channel> findAnyChannel(String orderType, int idx) {
        return channelListRepository.findAnyChannels(idx, findOrderType(orderType))
                .stream()
                .map(channelInfoInjectService::setTtl)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<Channel> findMyChannel(String orderType, String userId, int idx) {
        Users user = findUserById(userId);
        return channelListRepository.findMyChannels(user.getId(), idx, findOrderType(orderType))
                .stream()
                .map(channelInfoInjectService::setTtl)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<Channel> findByName(String tagName, String orderType, int idx) {
        HashTag hashTag = findHashTagByName(tagName);
        return channelListRepository.findChannelsByHashName(hashTag, idx, findOrderType(orderType))
                .stream()
                .map(channelInfoInjectService::setTtl)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<Channel> findChannelsRecentlyTalk(String orderType, int idx) {
        return channelListRepository.findChannelsRecentlyTalk(idx, findOrderType(orderType))
                .stream()
                .map(channelInfoInjectService::setTtl)
                .collect(toList());
    }

    private String findOrderType(String type) {
        if (orderMap.containsKey(type)) return orderMap.get(type);
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
