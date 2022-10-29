package webrtc.chatservice.service.pubsub;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.utils.json.CustomJsonMapper;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static webrtc.chatservice.enums.ChannelType.TEXT;

@Import({
        RedisSubscriberImpl.class
})
@ExtendWith(MockitoExtension.class)
public class RedisSubscriberImplTest {

    @InjectMocks
    private RedisSubscriberImpl redisSubscriber;

    @Mock
    private CustomJsonMapper objectMapper;
    @Mock
    private SimpMessageSendingOperations messagingTemplate;

    String message = "message";
    String nickname1 = "nickname1";
    String email1 = "email1";
    String channelName1 = "channelName1";
    ChannelType text = TEXT;

    @Test
    void 메시지전송성공() {
        // given

        // when
        redisSubscriber.sendMessage(message);

        // then

    }



    private Channel createChannel(String name, ChannelType type) {
        return Channel.builder()
                .channelName(name)
                .channelType(type)
                .build();
    }


    private ChattingMessage createChattingMessage() {
        Channel channel = createChannel(channelName1, text);
        return ChattingMessage.builder()
                .channelId(channel.getId())
                .senderName(nickname1)
                .chatMessage("message")
                .logId(1L)
                .users(new ArrayList<>())
                .currentParticipants(1L)
                .senderEmail(email1)
                .build();
    }

}
