package webrtc.chatservice.service.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.dto.rabbit.*;
import webrtc.chatservice.enums.ClientMessageType;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static webrtc.chatservice.enums.ClientMessageType.*;


@Component
@RequiredArgsConstructor
public class RabbitMessageFactoryImpl implements RabbitMessageFactory{
    private final Map<ClientMessageType, CreateRabbitMessage> messageTypes = new HashMap<>();
    private final EnterTypeRabbitMessage enterTypeRabbitMessage;
    private final CreateRabbitMessage createRabbitMessage;
    private final ChatTypeRabbitMessage chatTypeRabbitMessage;
    private final ExitTypeRabbitMessage exitTypeRabbitMessage;
    private final CloseTypeRagbbitMessage closeTypeRagbbitMessage;

    @PostConstruct
    public void messageFactoryConst() {
        this.messageTypes.put(ENTER, enterTypeRabbitMessage);
        this.messageTypes.put(CREATE, createRabbitMessage);
        this.messageTypes.put(CHAT, chatTypeRabbitMessage);
        this.messageTypes.put(EXIT, exitTypeRabbitMessage);
        this.messageTypes.put(CLOSE, closeTypeRagbbitMessage);
    }

    @Override
    public void execute(ChattingMessage serverMessage, ClientMessageType type) {
        this.messageTypes.get(type).send(serverMessage);
    }
}
