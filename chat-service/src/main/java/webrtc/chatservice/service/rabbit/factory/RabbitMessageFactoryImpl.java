package webrtc.chatservice.service.rabbit.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.service.rabbit.template.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static webrtc.chatservice.enums.ClientMessageType.*;


@Component
@RequiredArgsConstructor
public class RabbitMessageFactoryImpl implements RabbitMessageFactory{
    private final Map<ClientMessageType, RabbitMessageTemplate> messageTypes = new HashMap<>();
    private final EnterTypeRabbitMessageTemplate enterTypeRabbitMessage;
    private final CreateTypeRabbitMessageTemplate createTypeRabbitMessageTemplate;
    private final ChatTypeRabbitMessageTemplate chatTypeRabbitMessage;
    private final ExitTypeRabbitMessageTemplate exitTypeRabbitMessage;
    private final CloseTypeRagbbitMessageTemplate closeTypeRagbbitMessage;

    @PostConstruct
    public void messageFactoryConst() {
        this.messageTypes.put(ENTER, enterTypeRabbitMessage);
        this.messageTypes.put(CREATE, createTypeRabbitMessageTemplate);
        this.messageTypes.put(CHAT, chatTypeRabbitMessage);
        this.messageTypes.put(EXIT, exitTypeRabbitMessage);
        this.messageTypes.put(CLOSE, closeTypeRagbbitMessage);
    }

    @Override
    public void execute(ChattingMessage serverMessage, ClientMessageType type) {
        this.messageTypes.get(type).send(serverMessage);
    }
}