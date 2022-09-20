package webrtc.chatservice.service.rabbit.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Import;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.enums.ChannelType;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static webrtc.chatservice.enums.ChannelType.TEXT;

@Import({
        RabbitMessageTemplate.class
})
@ExtendWith(MockitoExtension.class)
public class RabbitMessageTemplateTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitMessageTemplate rabbitMessageTemplate = new ChatTypeRabbitMessageTemplate(rabbitTemplate);
    String nickname1 = "nickname1";
    String email1 = "email1";
    String channelName1 = "channelName1";
    ChannelType text = TEXT;


    @Test
    void 전송성공() throws JsonProcessingException {
        // given
        ChattingMessage message = createChattingMessage();


        // when

        // then
        rabbitMessageTemplate.send(message);
    }

    @Test
    void 전송실패() throws JsonProcessingException {
        // given
        ChattingMessage message = createChattingMessage();
//        doThrow(new Exception())
//                .when(objectMapper).writeValueAsString(any(ChattingMessage.class));

        // when

        // then
        rabbitMessageTemplate.send(message);

    }

    private Channel createChannel(String name, ChannelType type) {
        return new Channel(name, type);
    }


    private ChattingMessage createChattingMessage() {
        Channel channel = createChannel(channelName1, text);
        return new ChattingMessage(channel.getId(), nickname1, "message", 1L, new ArrayList<>(), 1L, email1);
    }
}
