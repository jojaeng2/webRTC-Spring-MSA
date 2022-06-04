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
    private Set<ChannelHashTag> channelHashTags;
    private static final Long serialVersionUID = 1L;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "channel")
    private List<ChatLog> chatLogs;


    public Channel(String channelName) {
        this.id = UUID.randomUUID().toString();
        this.channelName = channelName;
        this.limitParticipants = 15L;
        this.currentParticipants = 0L;
        this.timeToLive = 24*60*60L;
        this.channelUsers = new HashSet<>();
        this.channelHashTags = new HashSet<>();
        this.chatLogs = new ArrayList<>();
    }

    public void addChannelUser(ChannelUser channelUser) {
        this.currentParticipants++;
        channelUser.setChannel(this);
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

    public void setLimitParticipants(Long limitParticipants) {
        this.limitParticipants = limitParticipants;
    }

    public void addChatLog(ChatLog chatLog) {
        chatLog.setChannel(this);
        this.chatLogs.add(chatLog);
    }
}
