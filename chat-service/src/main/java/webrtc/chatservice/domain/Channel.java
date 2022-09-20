package webrtc.chatservice.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import webrtc.chatservice.enums.ChannelType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Getter
@Entity
@NoArgsConstructor
@RedisHash("channel")
public class Channel implements Serializable {

    @Id
    @Column(name = "channel_id")
    private String id;
    private String channelName;
    private Long limitParticipants;
    private Long currentParticipants;
    private Long timeToLive;
    private ChannelType channelType;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE)
    private Set<ChannelUser> channelUsers;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE)
    private List<ChannelHashTag> channelHashTags;

    private static final Long serialVersionUID = 1L;

    @OneToMany(mappedBy = "channel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChatLog> chatLogs;


    public Channel(String channelName, ChannelType channelType) {
        this.id = UUID.randomUUID().toString();
        this.channelName = channelName;
        this.limitParticipants = 15L;
        this.currentParticipants = 0L;
        this.timeToLive = 60L*60L;
        this.channelUsers = new HashSet<>();
        this.channelHashTags = new ArrayList<>();
        this.chatLogs = new ArrayList<>();
        this.channelType = channelType;
    }

    public void enterChannelUser(ChannelUser channelUser) {
        this.currentParticipants++;
        this.channelUsers.add(channelUser);
    }


    public void setTimeToLive(Long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public void addChannelHashTag(ChannelHashTag channelHashTag) {
        this.channelHashTags.add(channelHashTag);
    }

    public void exitChannelUser(ChannelUser channelUser) {
        this.currentParticipants--;
        this.channelUsers.remove(channelUser);
    }


    public void addChatLog(ChatLog chatLog) {
        chatLog.setChannel(this);
        this.chatLogs.add(chatLog);
    }
}
