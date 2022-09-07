package webrtc.chatservice.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webrtc.chatservice.dto.chat.*;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.service.channel.ChannelIOService;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static webrtc.chatservice.enums.ClientMessageType.*;

@Service
@RequiredArgsConstructor
public class SocketMessageFactoryImpl implements SocketMessageFactory{

    private final ChannelIOService channelIOService;
    private final Map<ClientMessageType, CreateClientMessage> messageTypes = new HashMap<>();

    @PostConstruct
    public void messageFactoryConst() {
        this.messageTypes.put(CHAT, new ChatTypeClientMessage());
        this.messageTypes.put(EXIT, new ExitTypeClientMessage(channelIOService));
        this.messageTypes.put(ENTER, new EnterTypeClientMessage());
    }

    public void execute(ClientMessageType type, ClientMessage overallMessage, String nickname, String userId, String channelId) {
        this.messageTypes.get(type).build(type, overallMessage, nickname, userId, channelId);
    }
}
