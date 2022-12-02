package webrtc.v1.repository.channel;

import webrtc.v1.domain.Channel;

public interface ChannelRedisRepository {
    void save(Channel channel);

    Long findTtl(String channelId);

    void extensionTtl(Channel channel, Long requestTTL);

    void delete(String channelId);
}
