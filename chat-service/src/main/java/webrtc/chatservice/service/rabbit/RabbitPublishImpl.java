package webrtc.chatservice.service.rabbit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.service.rabbit.factory.RabbitMessageFactory;

@Component
@RequiredArgsConstructor
public class RabbitPublishImpl implements RabbitPublish {
    private final RabbitMessageFactory rabbitMessageFactory;

    public void publishMessage(ChattingMessage serverMessage, ClientMessageType type) {
         rabbitMessageFactory.execute(serverMessage, type);
    }
}
