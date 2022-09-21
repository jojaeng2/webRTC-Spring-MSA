package webrtc.chatservice.service.rabbit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.service.rabbit.factory.RabbitMessageFactory;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitPublishImpl implements RabbitPublish {
    private final RabbitMessageFactory rabbitMessageFactory;

    public void publishMessage(ChattingMessage serverMessage, ClientMessageType type) {
         log.info(String.valueOf(serverMessage));
        rabbitMessageFactory.execute(serverMessage, type);
    }
}
