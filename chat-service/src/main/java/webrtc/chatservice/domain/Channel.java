package webrtc.chatservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import webrtc.chatservice.enums.ChannelType;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Channel implements Serializable {

    @Id
    @Column(name = "channel_id")
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    private String channelName;
    @Builder.Default
    private long limitParticipants = 15L;
    @Builder.Default
    private long currentParticipants = 0L;
    @Builder.Default
    private long timeToLive = 60L*60L;
    private ChannelType channelType;
    private Timestamp latestLog;


    @Builder.Default
    @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE)
    private List<ChannelUser> channelUsers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE)
    private List<ChannelHashTag> channelHashTags = new ArrayList<>();


    @Builder.Default
    @OneToMany(mappedBy = "channel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChatLog> chatLogs = new ArrayList<>();

    private static final long serialVersionUID = 1L;

    public void enterChannelUser(ChannelUser channelUser) {
        this.currentParticipants++;
        this.channelUsers.add(channelUser);
    }


    public void setTimeToLive(long timeToLive) {
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
        this.latestLog = chatLog.getSendTime();
    }

    public void setCurrentParticipants(long newone) {
        this.currentParticipants = newone;
    }
}
