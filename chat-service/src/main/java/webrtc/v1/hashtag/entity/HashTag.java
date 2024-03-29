package webrtc.v1.hashtag.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import webrtc.v1.channel.entity.ChannelHashTag;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = @Index(name = "tagName_index", columnList = "name"))
public class HashTag implements Serializable {

  @Id
  @Column(name = "hashtag_id")
  @JsonIgnore
  @Builder.Default
  private String id = UUID.randomUUID().toString();
  private String name;

  @OneToMany(mappedBy = "hashTag")
  @JsonIgnore
  @Builder.Default
  private List<ChannelHashTag> channelHashTags = new ArrayList<>();

  public void addChannelHashTag(ChannelHashTag channelHashTag) {
    this.channelHashTags.add(channelHashTag);
  }
}