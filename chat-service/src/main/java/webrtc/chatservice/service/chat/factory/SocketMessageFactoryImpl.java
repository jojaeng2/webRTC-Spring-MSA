package webrtc.chatservice.service.chat.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import webrtc.chatservice.dto.chat.*;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.service.channel.ChannelIOService;
import webrtc.chatservice.service.chat.template.ChatTypeClientMessageTemplate;
import webrtc.chatservice.service.chat.template.CreateClientMessageTemplate;
import webrtc.chatservice.service.chat.template.EnterTypeClientMessageTemplate;
import webrtc.chatservice.service.chat.template.ExitTypeClientMessageTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static webrtc.chatservice.enums.ClientMessageType.*;

@Component
@RequiredArgsConstructor
public class SocketMessageFactoryImpl implements SocketMessageFactory{

    private final ChannelIOService channelIOService;
    private final Map<ClientMessageType, CreateClientMessageTemplate> messageTypes = new HashMap<>();

    @PostConstruct
    public void messageFactoryConst() {
        this.messageTypes.put(CHAT, new ChatTypeClientMessageTemplate());
        this.messageTypes.put(EXIT, new ExitTypeClientMessageTemplate(channelIOService));
        this.messageTypes.put(ENTER, new EnterTypeClientMessageTemplate());
    }

    public void execute(ClientMessageType type, ClientMessage overallMessage, String nickname, String userId, String channelId) {
        this.messageTypes.get(type).build(type, overallMessage, nickname, userId, channelId);
    }
}
