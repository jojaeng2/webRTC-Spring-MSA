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

import static webrtc.chatservice.enums.ClientMessageType.*;

@Service
@RequiredArgsConstructor
public class ChattingMessageFactoryImpl implements ChattingMessageFactory{

    private Map<ClientMessageType, CreateMessage> messageTypes = new HashMap();

    @PostConstruct
    public void messageFactoryConst() {
        this.messageTypes.put(CHAT, new ChatTypeMessage());
        this.messageTypes.put(ENTER, new EnterTypeMessage());
        this.messageTypes.put(EXIT, new ExitTypeMessage());
        this.messageTypes.put(CLOSE, new CloseTypeMessage());
        this.messageTypes.put(REENTER, new ReenterTypeMessage());
        this.messageTypes.put(CREATE, new CreateTypeMessage());

    }
    @Override
    public ChattingMessage createMessage(String channelId, ClientMessageType type, String nickname, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail) {
        return this.messageTypes.get(type).setFields(channelId, nickname, chatMessage, currentParticipants, users, logId, senderEmail);
    }
}
