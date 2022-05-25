package webrtc.openvidu.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.openvidu.enums.ClientMessageType;

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
    @JsonIgnore
    private Channel channel;

    private Long idx;

    @Enumerated(EnumType.STRING)
    private ClientMessageType type;


    private String message;
    private String name;
    private Timestamp sendTime;

    public ChatLog(ClientMessageType type, String message, String name) {
        this.type = type;
        this.message = message;
        this.name = name;
        this.sendTime = new Timestamp(System.currentTimeMillis());
    }

    public void setChatLogIdx(Long idx) {
        this.idx = idx;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
