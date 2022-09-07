package webrtc.chatservice.service.chat;

import org.springframework.stereotype.Service;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.chat.*;
import webrtc.chatservice.enums.ClientMessageType;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static webrtc.chatservice.enums.SocketServerMessageType.*;
import static webrtc.chatservice.enums.SocketServerMessageType.CHAT;
import static webrtc.chatservice.enums.SocketServerMessageType.CLOSE;
import static webrtc.chatservice.enums.SocketServerMessageType.CREATE;

@Service
public class ChattingMessageFactoryImpl implements ChattingMessageFactory{

    private final Map<ClientMessageType, CreateChattingMessage> messageTypes = new HashMap();

    @PostConstruct
    public void messageFactoryConst() {
        this.messageTypes.put(ClientMessageType.CHAT, (ChattingMessage message) -> message.setType(CHAT));
        this.messageTypes.put(ClientMessageType.ENTER, (ChattingMessage message) -> message.setType(RENEWAL));
        this.messageTypes.put(ClientMessageType.EXIT, (ChattingMessage message) -> message.setType(RENEWAL));
        this.messageTypes.put(ClientMessageType.CLOSE, (ChattingMessage message) -> message.setType(CLOSE));
        this.messageTypes.put(ClientMessageType.REENTER, (ChattingMessage message) -> message.setType(RENEWAL));
        this.messageTypes.put(ClientMessageType.CREATE, (ChattingMessage message) -> message.setType(CREATE));
    }
    @Override
    public ChattingMessage createMessage(String channelId, ClientMessageType type, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail) {
        ChattingMessage chattingMessage = new ChattingMessage(channelId, nickname, chatMessage, currentParticipants, users, logId, senderEmail);
        return this.messageTypes.get(type).setMessageType(chattingMessage);
    }
}
