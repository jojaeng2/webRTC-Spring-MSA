package webrtc.chatservice.service.rabbit;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.service.rabbit.factory.RabbitMessageFactory;
import webrtc.chatservice.service.rabbit.factory.RabbitMessageFactoryImpl;
import webrtc.chatservice.service.rabbit.template.TypeRabbitMessageTemplate;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static webrtc.chatservice.enums.ChannelType.TEXT;

@Import({
        RabbitPublishImpl.class
})
@ExtendWith(MockitoExtension.class)
public class RabbitPublishTest {

    @InjectMocks
    private RabbitPublishImpl rabbitPublish;

    @Mock
    private RabbitMessageFactory rabbitMessageFactory;

    String nickname1 = "nickname1";
    String email1 = "email1";
    String channelName1 = "channelName1";
    ChannelType text = TEXT;

    @Test
    void 메시지전송성공() {
        // given
        doNothing()
                .when(rabbitMessageFactory).execute(any(ChattingMessage.class), any(ClientMessageType.class));
        // when

        // then
        rabbitPublish.publishMessage(createChattingMessage(), ClientMessageType.CHAT);
    }

    private Channel createChannel(String name, ChannelType type) {
        return new Channel(name, type);
    }


    private ChattingMessage createChattingMessage() {
        Channel channel = createChannel(channelName1, text);
        return new ChattingMessage(channel.getId(), nickname1, "message", 1L, new ArrayList<>(), 1L, email1);
    }

}
