package webrtc.v1.utils.listner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.DefaultMessage;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import webrtc.v1.channel.service.ChannelLifeService;
import webrtc.v1.channel.service.ChannelLifeServiceImpl;

@ExtendWith(MockitoExtension.class)
public class RedisKeyExpiredListenerTest {

  @InjectMocks
  private RedisKeyExpiredListener redisKeyExpiredListener;

  @Mock
  private ChannelLifeService channelLifeService;
  @Mock
  private RedisMessageListenerContainer redisMessageListener;



  @Test
  void 채널삭제() {
    // given
    doNothing()
        .when(channelLifeService).delete(any(String.class));

    byte[] channel = new byte[10];
    byte[] body = new byte[10];

    // when
    redisKeyExpiredListener.doHandleMessage(new DefaultMessage(channel, body));

    // then
  }
}
