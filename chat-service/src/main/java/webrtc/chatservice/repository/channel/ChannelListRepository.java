package webrtc.chatservice.repository.channel;

import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelHashTag;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.HashTag;

import java.util.List;

public interface ChannelListRepository {

//    void save(Channel channel);

//    Channel create(Channel channel, List<ChannelHashTag> hashTags);

//    void delete(Channel channel);


    void exitChannelUserInChannel(Channel channel, ChannelUser channelUser);

    List<Channel> findAnyChannels(int idx, String type);


    List<Channel> findMyChannels(String userId, int idx, String type);

//    Channel findChannelById(String id);

    List<Channel> findChannelsByChannelIdAndUserId(String channelId, String userId);

    List<Channel> findChannelsByHashName(HashTag hashTag, int idx, String type);

    Channel findChannelByChannelName(String channelName);

}
