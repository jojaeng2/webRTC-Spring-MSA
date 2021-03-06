package webrtc.chatservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
public class ChannelHashTag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "channel_hashtag_id")
    @JsonIgnore
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "hashtag_id")
    private HashTag hashTag;

    public void CreateChannelHashTag(Channel channel, HashTag hashTag) {
        this.channel = channel;
        this.hashTag = hashTag;
    }
}

