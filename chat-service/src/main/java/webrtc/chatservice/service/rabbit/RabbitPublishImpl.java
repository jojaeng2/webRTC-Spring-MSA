package webrtc.chatservice.service.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.enums.ClientMessageType;

@Component
@RequiredArgsConstructor
public class RabbitPublishImpl implements RabbitPublish {
    private final RabbitMessageFactory rabbitMessageFactory;

    public void publishMessage(ChattingMessage serverMessage, ClientMessageType type) {
         rabbitMessageFactory.execute(serverMessage, type);
    }
}
