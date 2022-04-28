package webrtc.openvidu.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Getter
@Entity
@NoArgsConstructor
public class Channel implements Serializable {
    @Id
    @Column(name = "channel_id")
    private String id;
    private String channelName;
    private Long limitParticipants;
    private Long currentParticipants;
    private Long timeToLive;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "channel")
    private List<ChannelUser> channelUsers;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "channel")
    private List<ChannelHashTag> channelHashTags = new ArrayList<ChannelHashTag>();
    private static final Long serialVersionUID = 1L;


    public Channel(String channelName) {
        this.id = UUID.randomUUID().toString();
        this.channelName = channelName;
        this.limitParticipants = 15L;
        this.currentParticipants = 1L;
        this.timeToLive = 24*60*60L;
        this.channelUsers = new ArrayList<>();
    }

    public void addUser(ChannelUser channelUser) {
        System.out.println(channelUser.getId());
        this.channelUsers.add(channelUser);
    }

    public void addChannelUser(ChannelUser channelUser) {
        this.channelUsers.add(channelUser);
    }

    public void setTimeToLive(Long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public void addChannelHashTag(ChannelHashTag channelHashTag) {
        this.channelHashTags.add(channelHashTag);
    }


}
