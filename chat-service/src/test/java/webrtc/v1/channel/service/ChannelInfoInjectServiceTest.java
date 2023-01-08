package webrtc.v1.channel.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.exception.ChannelException.NotExistChannelException;
import webrtc.v1.channel.repository.ChannelRedisRepository;
import webrtc.v1.staticgenarator.ChannelGenerator;

@ExtendWith(MockitoExtension.class)
public class ChannelInfoInjectServiceTest {

  @InjectMocks
  private ChannelInfoInjectServiceImpl channelInfoInjectService;

  @Mock
  private ChannelRedisRepository channelRedisRepository;

  private static final Long ttl = 10L;
  private static final Long expireTTL = -2L;

  @Test
  void 채널수명설정성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();
    doReturn(ttl)
        .when(channelRedisRepository).findTtl(any(String.class));

    // when
    Channel result = channelInfoInjectService.setTtl(channel);

    // then
    assertThat(channel.getTimeToLive()).isEqualTo(ttl);
  }

  @Test
  void 채널수명반환성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();

    doReturn(ttl)
        .when(channelRedisRepository).findTtl(any(String.class));

    // when
    long findTTL = channelInfoInjectService.findTtl(channel.getId());

    // then
    assertThat(findTTL).isEqualTo(ttl);
  }

  @Test
  void 채널수명반환실패() {

    Channel channel = ChannelGenerator.createTextChannel();

    doReturn(expireTTL)
        .when(channelRedisRepository).findTtl(any(String.class));

    // when

    // then
    org.junit.jupiter.api.Assertions.assertThrows(
        NotExistChannelException.class, () -> channelInfoInjectService.findTtl(channel.getId()));

  }
}
