package webrtc.chatservice.repository.channel;

import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelUser;

import java.util.List;

public interface ChannelRepository {

    void save(Channel channel);

    Channel createChannel(Channel channel, List<String> hashTags);

    void deleteChannel(Channel channel);

    void enterChannelUserInChannel(Channel channel, ChannelUser channelUser);

    void exitChannelUserInChannel(Channel channel, ChannelUser channelUser);

    List<Channel> findAnyChannelByPartiASC(int idx);
    List<Channel> findAnyChannelByPartiDESC(int idx);


    List<Channel> findMyChannelByPartiASC(String userId, int idx);
    List<Channel> findMyChannelByPartiDESC(String userId, int idx);

    List<Channel> findChannelsById(String id);

    List<Channel> findChannelsByChannelIdAndUserId(String channelId, String userId);

    List<Channel> findChannelsByHashNameAndPartiASC(String tagName, int idx);
    List<Channel> findChannelsByHashNameAndPartiDESC(String tagName, int idx);

    Channel findChannelByChannelName(String channelName);

    Long findChannelTTL(String channelId);

    void extensionChannelTTL(Channel channel, Long addTTL);

    Long getCurrentParticipants(Channel channel);

}
