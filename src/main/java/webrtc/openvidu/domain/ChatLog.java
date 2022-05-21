package webrtc.openvidu.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.openvidu.enums.ChatEnumType;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
public class ChatLog {

    @Id
    @Column(name = "chat_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    @JsonIgnore
    private Channel channel;

    @Enumerated(EnumType.STRING)
    private ChatEnumType type;


    private String message;
    private String name;
    private Timestamp sendTime;

    public ChatLog(ChatEnumType type, String message, String name) {
        this.type = type;
        this.message = message;
        this.name = name;
        this.sendTime = new Timestamp(System.currentTimeMillis());
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
        channel.getChatLogs().add(this);
    }
}
