package webrtc.chatservice.service.chat;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ChattingMessageFactoryImpl implements ChattingMessageFactory{

    private Map<ClientMessageType, CreateMessage> messageTypes = new HashMap();

    @PostConstruct
    public void messageFactoryConst() {
        this.messageTypes.put(ClientMessageType.CHAT, (String channelId, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail) ->
            new ChattingMessage(channelId, CHAT, nickname, chatMessage, currentParticipants, users, logId, senderEmail)
        );
        this.messageTypes.put(ClientMessageType.ENTER, (String channelId, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail) ->
            new ChattingMessage(channelId, RENEWAL, nickname, chatMessage, currentParticipants, users, logId, senderEmail)
        );
        this.messageTypes.put(ClientMessageType.EXIT, (String channelId, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail) ->
            new ChattingMessage(channelId, RENEWAL, nickname, chatMessage, currentParticipants, users, logId, senderEmail)
        );
        this.messageTypes.put(ClientMessageType.CLOSE, (String channelId, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail) ->
            new ChattingMessage(channelId, CLOSE, nickname, chatMessage, currentParticipants, users, logId, senderEmail)
        );
        this.messageTypes.put(ClientMessageType.REENTER, (String channelId, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail) ->
            new ChattingMessage(channelId, RENEWAL, nickname, chatMessage, currentParticipants, users, logId, senderEmail)
        );
        this.messageTypes.put(ClientMessageType.CREATE, (String channelId, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail) ->
            new ChattingMessage(channelId, CREATE, nickname, chatMessage, currentParticipants, users, logId, senderEmail)
        );
    }
    @Override
    public ChattingMessage createMessage(String channelId, ClientMessageType type, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail) {
        return this.messageTypes.get(type).setFields(channelId, nickname, chatMessage, currentParticipants, users, logId, senderEmail);
    }
}
