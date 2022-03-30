package webrtc.openvidu.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
public class ChannelHashTag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "channel_hashtag_id")
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "hashtag_id")
    private HashTag hashTag;

    public void CreateChannelHashTag(Channel channel, HashTag hashTag) {
        this.channel = channel;
        this.hashTag = hashTag;
    }
}
