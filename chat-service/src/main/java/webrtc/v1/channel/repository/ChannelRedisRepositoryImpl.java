package webrtc.v1.channel.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import webrtc.v1.channel.entity.Channel;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

import static webrtc.v1.channel.enums.ChannelInfo.CREATE_TTL;

@RequiredArgsConstructor
@Repository
public class ChannelRedisRepositoryImpl implements ChannelRedisRepository {

  private final RedisTemplate<String, Object> redisTemplate;
  private ValueOperations<String, Object> opsValueOperation;

  @PostConstruct
  private void init() {
    opsValueOperation = redisTemplate.opsForValue();
  }

  public void save(Channel channel) {
    opsValueOperation.set(channel.getId(), channel);
    redisTemplate.expire(channel.getId(), CREATE_TTL.getTtl(), TimeUnit.SECONDS);
  }


  public Long findTtl(String channelId) {
    return redisTemplate.getExpire(channelId);
  }

  public void extensionTtl(Channel channel, Long requestTTL) {
    long newTTL = findTtl(channel.getId()) + requestTTL;
    channel.setTimeToLive(newTTL);
    redisTemplate.expire(channel.getId(), newTTL, TimeUnit.SECONDS);
  }

  public void delete(String channelId) {
    redisTemplate.delete(channelId);
  }
}
