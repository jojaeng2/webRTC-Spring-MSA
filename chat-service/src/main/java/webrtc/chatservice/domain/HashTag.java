package webrtc.chatservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@RedisHash("hashTag")
public class HashTag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    @JsonIgnore
    private Long id;
    private String tagName;

    @OneToMany(mappedBy = "hashTag")
    @JsonIgnore
    private List<ChannelHashTag> channelHashTags;

    public HashTag(String tagName) {
        this.tagName = tagName;
        this.channelHashTags = new ArrayList<>();
    }

    public void addChannelHashTag(ChannelHashTag channelHashTag) {
        this.channelHashTags.add(channelHashTag);
    }



}