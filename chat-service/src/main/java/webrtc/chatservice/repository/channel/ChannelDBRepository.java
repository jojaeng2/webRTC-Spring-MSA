package webrtc.chatservice.repository.channel;

import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelHashTag;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.HashTag;

import java.util.List;

public interface ChannelDBRepository {

    void save(Channel channel);

    Channel createChannel(Channel channel, List<ChannelHashTag> hashTags);

    void deleteChannel(Channel channel);


    void exitChannelUserInChannel(Channel channel, ChannelUser channelUser);

    List<Channel> findAnyChannelByPartiASC(int idx);
    List<Channel> findAnyChannelByPartiDESC(int idx);


    List<Channel> findMyChannelByPartiASC(String userId, int idx);
    List<Channel> findMyChannelByPartiDESC(String userId, int idx);

    Channel findChannelById(String id);

    List<Channel> findChannelsByChannelIdAndUserId(String channelId, String userId);

    List<Channel> findChannelsByHashNameAndPartiASC(HashTag hashTag, int idx);
    List<Channel> findChannelsByHashNameAndPartiDESC(HashTag hashTag, int idx);

    Channel findChannelByChannelName(String channelName);



}
