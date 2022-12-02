package webrtc.v1.service.chat.factory;

import org.springframework.stereotype.Component;
import webrtc.v1.domain.Channel;
import webrtc.v1.domain.Users;
import webrtc.v1.dto.chat.*;
import webrtc.v1.enums.ClientMessageType;
import webrtc.v1.service.chat.template.MessageTypeTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static webrtc.v1.enums.SocketServerMessageType.*;
import static webrtc.v1.enums.SocketServerMessageType.CHAT;
import static webrtc.v1.enums.SocketServerMessageType.CLOSE;
import static webrtc.v1.enums.SocketServerMessageType.CREATE;

@Component
public class ChattingMessageFactoryImpl implements ChattingMessageFactory{

    private final Map<ClientMessageType, MessageTypeTemplate> messageTypes = new HashMap<>();

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
    public ChattingMessage createMessage(Channel channel, ClientMessageType type, String chatMessage, List<Users> users, long logId, Users user) {
        ChattingMessage chattingMessage = ChattingMessage.builder()
                .channelId(channel.getId())
                .senderName(user.getNickname())
                .chatMessage(chatMessage)
                .currentParticipants(channel.getCurrentParticipants())
                .users(users)
                .logId(logId)
                .senderEmail(user.getEmail())
                .build();
        return this.messageTypes.get(type).setMessageType(chattingMessage);
    }
}
