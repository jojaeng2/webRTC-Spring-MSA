package webrtc.v1.repository.channel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import webrtc.v1.channel.repository.ChannelRedisRepository;
import webrtc.v1.channel.repository.ChannelRedisRepositoryImpl;
import webrtc.v1.config.RedisConfig;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.enums.ChannelType;
import webrtc.v1.utils.pubsub.RedisSubscriberImpl;
import webrtc.config.TestRedisConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static webrtc.v1.channel.enums.ChannelType.TEXT;

@RunWith(SpringRunner.class)
@Import({
        RedisConfig.class,
        TestRedisConfig.class,
        ChannelRedisRepositoryImpl.class
}) // DataRedisTest로 찾지 못하는 Config 클래스를 import
@DataRedisTest
public class ChannelRedisRepositoryImplTest {

    @MockBean
    private RedisSubscriberImpl redisSubscriberImpl;
    @Autowired
    private ChannelRedisRepository channelRedisRepository;

    String channelName1 = "channelName1";
    ChannelType text = TEXT;

    @Test
    public void 채널생성_생성성공() {
        // given
        Channel channel = Channel.builder()
                .channelName(channelName1)
                .channelType(text)
                .build();

        // when
        channelRedisRepository.save(channel);
        channelRedisRepository.delete(channel.getId());
        // then
    }

    @Test
    public void 채널생성_생성실패() {
        // given

        // when

        // then
        assertThrows(NullPointerException.class, ()-> {
            channelRedisRepository.save(null);
        });
    }

    @Test
    public void 채널수명_반환성공() {
        // given
        Channel channel = Channel.builder()
                .channelName(channelName1)
                .channelType(text)
                .build();
        channelRedisRepository.save(channel);

        // when
        Long channelTTL = channelRedisRepository.findTtl(channel.getId());
        channelRedisRepository.delete(channel.getId());

        // then
        assertThat(channelTTL).isGreaterThan(0L);
    }

    @Test
    public void 채널수명_반환실패() {
        // given

        // when
        // then
        assertThrows(IllegalArgumentException.class, ()-> {
            channelRedisRepository.findTtl(null);
        });
    }

    @Test
    public void 채널수명_연장성공() {
        // given
        Channel channel = Channel.builder()
                .channelName(channelName1)
                .channelType(text)
                .build();
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
        Channel channel = Channel.builder()
                .channelName(channelName1)
                .channelType(text)
                .build();
        channelRedisRepository.save(channel);

        // when
        channelRedisRepository.delete(channel.getId());

        // then
    }
}
