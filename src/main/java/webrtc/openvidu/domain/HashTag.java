package webrtc.openvidu.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class HashTag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    @JsonIgnore
    private Long id;
    private String tagName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hashTag")
    @JsonIgnore
    private List<ChannelHashTag> channelHashTags = new ArrayList<ChannelHashTag>();

    public void addChannelHashTag(ChannelHashTag channelHashTag) {
        this.channelHashTags.add(channelHashTag);
    }

    public HashTag(String tagName) {
        this.tagName = tagName;
    }


}