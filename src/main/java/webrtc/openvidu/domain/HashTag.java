package webrtc.openvidu.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class HashTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Long id;
    private String tagName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hashTag")
    private List<ChannelHashTag> channelHashTags = new ArrayList<ChannelHashTag>();

}
