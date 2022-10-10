package webrtc.chatservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@AllArgsConstructor
@Builder
@RedisHash("chatLog")
public class ChatLog {

    @Id
    @Column(name = "chat_id")
    @JsonIgnore
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "channel_id")
    @JsonIgnore
    private Channel channel;

    @Builder.Default
    private Long idx = 1L;

    @Enumerated(EnumType.STRING)
    private ClientMessageType type;
    private String message;
    private String senderNickname;
    private String senderEmail;

    @Builder.Default
    private Timestamp sendTime = new Timestamp(System.currentTimeMillis());
    
    public void setChatLogIdx(Long idx) {
        this.idx = idx;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
