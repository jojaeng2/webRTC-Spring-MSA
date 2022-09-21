package webrtc.chatservice.repository.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.exception.ChannelException;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class ChannelRedisRepositoryImpl implements ChannelRedisRepository{

    private final RedisTemplate<String, Object> redisTemplate;
    private  ValueOperations<String, Object> opsValueOperation;
    private final long channelTTL = 1L * 30L;

    @PostConstruct
    private void init() {
        opsValueOperation = redisTemplate.opsForValue();
    }

    public void createChannel(Channel channel) {
        opsValueOperation.set(channel.getId(), channel);
        redisTemplate.expire(channel.getId(), channelTTL, TimeUnit.SECONDS);
    }


    public Long findChannelTTL(String channelId) {
        return redisTemplate.getExpire(channelId);
    }

    public void extensionChannelTTL(Channel channel, Long requestTTL) {
        long newTTL = findChannelTTL(channel.getId()) + requestTTL;
        channel.setTimeToLive(newTTL);
        redisTemplate.expire(channel.getId(), newTTL, TimeUnit.SECONDS);
    }

    public void delete(String channelId) {
        redisTemplate.delete(channelId);
    }
}
