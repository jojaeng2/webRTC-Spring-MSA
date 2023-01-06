package webrtc.v1.channel.repository;

import webrtc.v1.channel.entity.Channel;

public interface ChannelRedisRepository {

  void save(Channel channel);

  Long findTtl(String channelId);

  void extensionTtl(Channel channel, Long requestTTL);

  void delete(String channelId);
}
