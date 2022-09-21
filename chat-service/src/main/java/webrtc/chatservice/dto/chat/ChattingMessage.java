package webrtc.chatservice.dto.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.enums.SocketServerMessageType;

import java.sql.Timestamp;
import java.util.List;

@NoArgsConstructor
@Getter
public class ChattingMessage {
    private String channelId;
    private SocketServerMessageType type;
    private String senderName;
    private String chatMessage;
    private Long currentParticipants;
    private List<Users> users;
    private Long logId;
    private String senderEmail;
    private Timestamp sendTime;


    public ChattingMessage(String channelId, String senderName, String chatMessage, Long currentParticipants, List<Users> users, Long logId, String senderEmail) {
        this.channelId = channelId;
        this.senderName = senderName;
        this.chatMessage = chatMessage;
        this.currentParticipants = currentParticipants;
        this.users = users;
        this.logId = logId;
        this.senderEmail = senderEmail;
        this.sendTime = new Timestamp(System.currentTimeMillis());
    }

    public ChattingMessage setType(SocketServerMessageType type) {
        this.type = type;
        return this;
    }
}
