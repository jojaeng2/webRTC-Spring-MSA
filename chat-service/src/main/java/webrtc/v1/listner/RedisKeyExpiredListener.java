package webrtc.v1.listner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import webrtc.v1.domain.Users;
import webrtc.v1.enums.ClientMessageType;
import webrtc.v1.repository.voice.VoiceRoomRepository;
import webrtc.v1.service.channel.ChannelLifeService;
import webrtc.v1.service.chat.ChattingService;

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
        chattingService.sendChatMessage(ClientMessageType.CLOSE, message.toString(), "채팅방의 수명이 끝났습니다.", userBuilder());
        channelLifeService.delete(message.toString());
    }

    Users userBuilder() {
        return Users.builder()
                .nickname("[알림] ")
                .email("Notice")
                .build();
    }
}
