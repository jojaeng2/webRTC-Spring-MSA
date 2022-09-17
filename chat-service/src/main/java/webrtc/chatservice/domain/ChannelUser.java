package webrtc.chatservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor
@RedisHash("channelUser")
public class ChannelUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "channel_user_id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    public ChannelUser(Users user, Channel channel) {
        this.channel = channel;
        this.user = user;
        channel.enterChannelUser(this);
        user.addChannelUser(this);
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setUsers(Users user) {
        this.user = user;
    }
}
