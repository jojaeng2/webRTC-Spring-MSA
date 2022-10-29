package webrtc.chatservice.dto.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.enums.SocketServerMessageType;

import java.sql.Timestamp;
import java.util.List;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class ChattingMessage {
    private String channelId;
    private SocketServerMessageType type;
    private String senderName;
    private String chatMessage;
    private Long currentParticipants;
    private List<Users> users;
    private long logId;
    private String senderEmail;

    @Builder.Default
    private Timestamp sendTime = new Timestamp(System.currentTimeMillis());

    public ChattingMessage setType(SocketServerMessageType type) {
        this.type = type;
        return this;
    }
}
