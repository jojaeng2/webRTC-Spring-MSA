package webrtc.openvidu.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import webrtc.openvidu.domain.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Getter
@Entity
public class Channel implements Serializable {
    @Id
    @Column(name = "channel_id")
    private String id;
    private String channelName;
    private Long limitParticipants;
    private Long currentParticipants;
    private Long timeToLive;
//    private Map<Long, User> users;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "channel")
    private List<ChannelHashTag> channelHashTags = new ArrayList<ChannelHashTag>();
    private static final Long serialVersionUID = 1L;

    public Channel(String channelName, Long limitParticipants) {
        this.id = UUID.randomUUID().toString();
        this.channelName = channelName;
        this.limitParticipants = limitParticipants;
        this.currentParticipants = 1L;
        this.timeToLive = 24*60*60L;
//        this.users = new HashMap<>();
    }

    public void addUser(User user) {
//        this.users.put(user.getUserId(), user);
    }

    public void delUser(User user) {
//        this.users.remove(user.getUserId());
    }

    public void setTimeToLive(Long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public void addChannelHashTag(ChannelHashTag channelHashTag) {
        this.channelHashTags.add(channelHashTag);
    }
}
