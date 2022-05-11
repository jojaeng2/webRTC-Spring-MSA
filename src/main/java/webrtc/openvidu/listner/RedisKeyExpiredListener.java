package webrtc.openvidu.listner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import webrtc.openvidu.enums.ClientMessageType;
import webrtc.openvidu.service.channel.ChannelServiceImpl;
import webrtc.openvidu.service.chat.ChatServiceImpl;

@Component

public class RedisKeyExpiredListener extends KeyExpirationEventMessageListener {

    @Autowired
    private ChatServiceImpl chatService;
    @Autowired
    private ChannelServiceImpl channelServiceImpl;

    public RedisKeyExpiredListener(@Qualifier("redisMessageListener")RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void doHandleMessage(org.springframework.data.redis.connection.Message message) {
        chatService.sendChatMessage(ClientMessageType.CLOSE, message.toString(), "[알림]", "serverclose");
        channelServiceImpl.deleteChannel(message.toString());
    }
}
