package webrtc.v1.chat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.user.entity.Users;
import webrtc.v1.chat.enums.ClientMessageType;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

import static webrtc.v1.chat.enums.ChatLogEnums.*;
import static webrtc.v1.chat.enums.ClientMessageType.CREATE;

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
    private long idx = 1L;

    @Enumerated(EnumType.STRING)
    private ClientMessageType type;
    private String message;
    private String senderNickname;
    private String senderEmail;

    @Builder.Default
    private Timestamp sendTime = new Timestamp(System.currentTimeMillis());

    public static ChatLog createChannelLog(Users user) {
        return ChatLog.builder()
                .type(CREATE)
                .message(NOTICE.getMessage() + user.getNickname() + MESSAGE.getMessage())
                .senderNickname(NICKNAME.getMessage())
                .senderEmail(EMAIL.getMessage())
                .build();
    }

    public void setChatLogIdx(long idx) {
        this.idx = idx;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
