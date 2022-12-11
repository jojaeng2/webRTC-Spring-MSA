package webrtc.v1.channel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.v1.hashtag.entity.HashTag;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChannelHashTag implements Serializable {

    @Id
    @Column(name = "channel_hashtag_id")
    @JsonIgnore
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHANNEL_ID")
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "HASHTAG_ID")
    private HashTag hashTag;

}

