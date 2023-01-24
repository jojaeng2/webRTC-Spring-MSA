package webrtc.v1.utils.listner;

import static webrtc.v1.chat.enums.RedisKeys.CHAT_LOG;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import webrtc.v1.channel.service.ChannelLifeService;
import webrtc.v1.chat.repository.ChatLogRedisRepository;
import webrtc.v1.chat.repository.ChatLogRedisRepositoryImpl;

@Component
public class RedisKeyExpiredListener extends KeyExpirationEventMessageListener {

  private final ChannelLifeService channelLifeService;
  private final ChatLogRedisRepositoryImpl chatLogRedisRepository;
  public RedisKeyExpiredListener(
      @Qualifier("redisMessageListener") RedisMessageListenerContainer listenerContainer,
      ChannelLifeService channelLifeService,
      ChatLogRedisRepositoryImpl chatLogRedisRepository) {
    super(listenerContainer);
    this.channelLifeService = channelLifeService;
    this.chatLogRedisRepository = chatLogRedisRepository;
  }

  @Override
  public void doHandleMessage(org.springframework.data.redis.connection.Message message) {
    String key = message.toString();
    if (key.contains(CHAT_LOG.getPrefix())) {
      chatLogRedisRepository.delete(key);
    } else {
      channelLifeService.delete(key);
    }
  }
}
