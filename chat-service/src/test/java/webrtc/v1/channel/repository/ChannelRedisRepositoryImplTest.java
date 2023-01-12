package webrtc.v1.channel.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.config.RedisConfig;
import webrtc.v1.staticgenarator.ChannelGenerator;
import webrtc.v1.utils.pubsub.RedisSubscriberImpl;

@RunWith(SpringRunner.class)
@DataRedisTest
@Import({
    RedisConfig.class,
    ChannelRedisRepositoryImpl.class
})
@ExtendWith(MockitoExtension.class)
public class ChannelRedisRepositoryImplTest {

  @MockBean
  private RedisSubscriberImpl redisSubscriber;
  @Autowired
  private ChannelRedisRepository channelRedisRepository;
  @Mock
  private RedisTemplate<String, Object> redisTemplate;
  @Mock
  private ValueOperations<String, Object> opsValueOperation;

  @Test
  public void 채널생성_생성성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();
    doNothing()
        .when(opsValueOperation).set(any(String.class), any(Channel.class));
    doReturn(true)
        .when(redisTemplate).expire(any(String.class), any(Long.class), any(TimeUnit.class));

    // when
    channelRedisRepository.save(channel);

    // then
  }

  @Test
  public void 채널생성_생성실패() {
    // given

    // when

    // then
    assertThrows(NullPointerException.class, () -> {
      channelRedisRepository.save(null);
    });
  }

  @Test
  public void 채널수명_반환성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();
    doReturn(100L)
        .when(redisTemplate).getExpire(any(String.class));

    // when
    Long channelTTL = channelRedisRepository.findTtl(channel.getId());

    // then
    assertThat(channelTTL).isEqualTo(-2L);
  }

  @Test
  public void 채널수명_반환실패() {
    // given

    // when
    // then
    assertThrows(IllegalArgumentException.class, () -> {
      channelRedisRepository.findTtl(null);
    });
  }

  @Test
  public void 채널수명_연장성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();
    channelRedisRepository.save(channel);
    Long startTTL = channelRedisRepository.findTtl(channel.getId());

    // when
    channelRedisRepository.extensionTtl(channel, 1000L);
    Long endTTL = channelRedisRepository.findTtl(channel.getId());
    channelRedisRepository.delete(channel.getId());

    // then
    assertThat(endTTL).isGreaterThan(startTTL);
  }

  @Test
  public void 채널삭제성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();
    channelRedisRepository.save(channel);

    // when
    channelRedisRepository.delete(channel.getId());

    // then
  }

  @Test
  public void findTtl성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();

    // when
    Long ttl = channelRedisRepository.findTtl(channel.getId());

    // then
    Assertions.assertThat(ttl).isEqualTo(-2L);
  }
}
