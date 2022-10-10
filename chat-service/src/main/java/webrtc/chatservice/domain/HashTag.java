package webrtc.chatservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HashTag implements Serializable {

    @Id
    @Column(name = "hashtag_id")
    @JsonIgnore
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    private String tagName;

    @OneToMany(mappedBy = "hashTag")
    @JsonIgnore
    @Builder.Default
    private List<ChannelHashTag> channelHashTags = new ArrayList<>();

    public void addChannelHashTag(ChannelHashTag channelHashTag) {
        this.channelHashTags.add(channelHashTag);
    }



}