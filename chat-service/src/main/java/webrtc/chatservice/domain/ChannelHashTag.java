package webrtc.chatservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
//@RedisHash("channelHashTag")
public class ChannelHashTag implements Serializable {

    @Id
    @Column(name = "channel_hashtag_id")
    @JsonIgnore
    private String id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "hashtag_id")
    private HashTag hashTag;

    public ChannelHashTag(Channel channel, HashTag hashTag) {
        this.id = UUID.randomUUID().toString();
        this.channel = channel;
        this.hashTag = hashTag;
        channel.addChannelHashTag(this);
        hashTag.addChannelHashTag(this);
    }
}

