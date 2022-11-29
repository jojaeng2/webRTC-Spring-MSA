package webrtc.chatservice.listner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.service.channel.ChannelLifeService;
import webrtc.chatservice.service.chat.ChattingService;

@Component
public class RedisKeyExpiredListener extends KeyExpirationEventMessageListener {

    @Autowired
    private ChattingService chattingService;
    @Autowired
    private ChannelLifeService channelLifeService;

    public RedisKeyExpiredListener(@Qualifier("redisMessageListener")RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void doHandleMessage(org.springframework.data.redis.connection.Message message) {
        chattingService.sendChatMessage(ClientMessageType.CLOSE, message.toString(), "채팅방의 수명이 끝났습니다.",
                Users.builder()
                .nickname("[알림] ")
                .email("Notice")
                .build());
        channelLifeService.deleteChannel(message.toString());
    }
}
