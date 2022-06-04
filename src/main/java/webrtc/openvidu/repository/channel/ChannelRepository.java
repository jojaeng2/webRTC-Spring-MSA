package webrtc.openvidu.repository.channel;

import webrtc.openvidu.domain.Channel;

import java.util.List;

public interface ChannelRepository {

    void save(Channel channel);

    Channel createChannel(Channel channel, List<String> hashTags);

    void deleteChannel(Channel channel);

    Channel updateChannel(Channel channel);

    List<Channel> findAnyChannel(int idx);

    List<Channel> findMyChannel(String userId, int idx);

    List<Channel> findChannelsById(String id);

    List<Channel> findChannelsByUserId(String channelId, String userId);

    List<Channel> findChannelsByHashName(String tagName);

    List<Channel> findChannelsByChannelName(String channelName);

    Long findChannelTTL(String channelId);

    void extensionChannelTTL(Channel channel, Long addTTL);

    Long getCurrentParticipants(Channel channel);

}
