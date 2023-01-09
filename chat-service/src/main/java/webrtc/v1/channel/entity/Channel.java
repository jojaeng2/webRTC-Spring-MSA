package webrtc.v1.channel.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.v1.chat.entity.ChatLog;
import webrtc.v1.channel.enums.ChannelType;

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

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "channel_id")
  @Builder.Default
  private String id = UUID.randomUUID().toString();
  private String channelName;
  @Builder.Default
  private int limitParticipants = 15;
  @Builder.Default
  private int currentParticipants = 0;
  @Builder.Default
  private long timeToLive = 60L * 60L;
  private ChannelType channelType;
  private Timestamp latestLog;

  @Builder.Default
  @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE)
  private List<ChannelUser> channelUsers = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE)
  private List<ChannelHashTag> channelHashTags = new ArrayList<>();


  public void enterChannelUser(ChannelUser channelUser) {
    this.currentParticipants++;
    this.channelUsers.add(channelUser);
    channelUser.getUser().addChannelUser(channelUser);
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
    channelUser.getUser().exitChannelUser(channelUser);
  }


  public void setLatestLog(ChatLog chatLog) {
    this.latestLog = chatLog.getSendTime();
  }

  public void setCurrentParticipants(int participants) {
    this.currentParticipants = participants;
  }

  public boolean isFull() {
    return this.limitParticipants == this.currentParticipants;
  }
}
