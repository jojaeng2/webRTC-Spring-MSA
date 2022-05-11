package webrtc.openvidu.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

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
    private Set<ChannelUser> channelUsers;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "channel")
    private Set<ChannelHashTag> channelHashTags = new HashSet<>();
    private static final Long serialVersionUID = 1L;


    public Channel(String channelName) {
        this.id = UUID.randomUUID().toString();
        this.channelName = channelName;
        this.limitParticipants = 15L;
        this.currentParticipants = 1L;
        this.timeToLive = 24*60*60L;
        this.channelUsers = new HashSet<>();
    }

    public void addChannelUser(ChannelUser channelUser) {
        this.currentParticipants++;
        channelUser.setChannel(this);
    }

    @Transactional
    public void removeChannelUser(ChannelUser channelUser) {
        this.channelUsers.remove(channelUser);
    }

    public void setTimeToLive(Long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public void addChannelHashTag(ChannelHashTag channelHashTag) {
        this.channelHashTags.add(channelHashTag);
    }

    public void minusCurrentParticipants() {
        this.currentParticipants--;
    }
}
