package webrtc.openvidu.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
public class ChatLog {

    @Id
    @Column(name = "chat_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    private String message;
    private String name;
    private Timestamp sendTime;

    public ChatLog(String message, String name) {
        this.message = message;
        this.name = name;
        this.sendTime = new Timestamp(System.currentTimeMillis());
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
        channel.getChatLogs().add(this);
    }
}
