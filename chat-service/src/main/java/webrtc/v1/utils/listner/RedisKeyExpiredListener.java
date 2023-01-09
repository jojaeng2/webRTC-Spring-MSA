package webrtc.v1.utils.listner;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import webrtc.v1.channel.service.ChannelLifeService;

@Component
public class RedisKeyExpiredListener extends KeyExpirationEventMessageListener {

  private final ChannelLifeService channelLifeService;

  public RedisKeyExpiredListener(
      @Qualifier("redisMessageListener") RedisMessageListenerContainer listenerContainer,
      ChannelLifeService channelLifeService) {
    super(listenerContainer);
    this.channelLifeService = channelLifeService;
  }

  @Override
  public void doHandleMessage(org.springframework.data.redis.connection.Message message) {
    channelLifeService.delete(message.toString());
  }
}
