package webrtc.v1.channel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.v1.hashtag.entity.HashTag;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChannelHashTag implements Serializable {

  @Id
  @Column(name = "channel_hashtag_id")
  @JsonIgnore
  @Builder.Default
  private String id = UUID.randomUUID().toString();

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id")
  private Channel channel;

  @ManyToOne
  @JoinColumn(name = "hashtag_id")
  private HashTag hashTag;

}

