package webrtc.chatservice.service.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.dto.rabbit.CreateRabbitMessage;
import webrtc.chatservice.enums.ClientMessageType;
import static webrtc.chatservice.config.RabbitmqConfig.*;

@Component
@RequiredArgsConstructor
public class RabbitPublishImpl implements RabbitPublish {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final RabbitMessageFactory rabbitMessageFactory;

    public void publishMessage(ChattingMessage serverMessage, ClientMessageType type) {
         rabbitMessageFactory.execute(serverMessage, type);
    }
}
