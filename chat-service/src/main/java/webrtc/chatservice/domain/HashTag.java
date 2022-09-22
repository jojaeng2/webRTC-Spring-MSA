package webrtc.chatservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor
public class HashTag implements Serializable {

    @Id
    @Column(name = "hashtag_id")
    @JsonIgnore
    private String id;
    private String tagName;

    @OneToMany(mappedBy = "hashTag")
    @JsonIgnore
    private List<ChannelHashTag> channelHashTags;

    public HashTag(String tagName) {
        this.id = UUID.randomUUID().toString();
        this.tagName = tagName;
        this.channelHashTags = new ArrayList<>();
    }

    public void addChannelHashTag(ChannelHashTag channelHashTag) {
        this.channelHashTags.add(channelHashTag);
    }



}