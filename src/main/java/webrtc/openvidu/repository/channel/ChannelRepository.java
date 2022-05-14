package webrtc.openvidu.repository.channel;

import webrtc.openvidu.domain.Channel;

import java.util.List;

public interface ChannelRepository {

    void save(Channel channel);

    Channel createChannel(Channel channel, List<String> hashTags);

    void deleteChannel(Channel channel);

    Channel updateChannel(Channel channel);

    List<Channel> findAllChannel();

    List<Channel> findChannelsById(String id);

    List<Channel> findChannelsByUserId(String channelId, String userId);

    List<Channel> findChannelsByHashName(String tagName);

    List<Channel> findChannelsByChannelName(String channelName);

    Long findChannelTTL(String channelId);

    Long getCurrentParticipants(Channel channel);

    Long updateCurrentParticipants(Channel channel);
}
