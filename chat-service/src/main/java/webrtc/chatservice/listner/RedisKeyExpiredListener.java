package webrtc.chatservice.listner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.service.channel.ChannelService;
import webrtc.chatservice.service.chat.ChatService;

@Component
public class RedisKeyExpiredListener extends KeyExpirationEventMessageListener {

    @Autowired
    private ChatService chatService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private HttpApiController httpApiController;

    public RedisKeyExpiredListener(@Qualifier("redisMessageListener")RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void doHandleMessage(org.springframework.data.redis.connection.Message message) {
        chatService.sendChatMessage(ClientMessageType.CLOSE, message.toString(), "[알림] ", "채팅방의 수명이 끝났습니다.", "Notice");
        channelService.deleteChannel(message.toString());
        httpApiController.postDeletedChannel(message.toString());
    }
}
