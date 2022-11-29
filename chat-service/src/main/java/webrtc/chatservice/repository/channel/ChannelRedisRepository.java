package webrtc.chatservice.repository.channel;

import webrtc.chatservice.domain.Channel;

public interface ChannelRedisRepository {
    void save(Channel channel);

    Long findChannelTTL(String channelId);

    void extensionChannelTTL(Channel channel, Long requestTTL);

    void delete(String channelId);
}
