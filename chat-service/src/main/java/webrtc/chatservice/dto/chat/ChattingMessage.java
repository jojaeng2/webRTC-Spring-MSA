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
    private Long logId;
    private String senderEmail;
    @JsonIgnore
    @Builder.Default
    private String ip = "Not Found";
    @JsonIgnore
    @Builder.Default
    private String browser = "Not Found";
    @Builder.Default
    private Timestamp sendTime = new Timestamp(System.currentTimeMillis());

    public ChattingMessage setType(SocketServerMessageType type) {
        this.type = type;
        return this;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }
}
