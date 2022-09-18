package webrtc.chatservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import webrtc.chatservice.enums.ClientMessageType;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@RedisHash("chatLog")
public class ChatLog {

    @Id
    @Column(name = "chat_id")
    @JsonIgnore
    private String id;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    @JsonIgnore
    private Channel channel;

    private Long idx;

    @Enumerated(EnumType.STRING)
    private ClientMessageType type;
    private String message;
    private String senderNickname;
    private String senderEmail;
    private Timestamp sendTime;

    public ChatLog(ClientMessageType type, String message, String senderNickname, String senderEmail) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.message = message;
        this.senderNickname = senderNickname;
        this.senderEmail = senderEmail;
        this.sendTime = new Timestamp(System.currentTimeMillis());
        this.idx = 1L;
    }

    public void setChatLogIdx(Long idx) {
        this.idx = idx;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
