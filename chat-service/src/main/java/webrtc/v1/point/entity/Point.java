package webrtc.v1.point.entity;

import static webrtc.v1.point.enums.PointMessage.CREATE;
import static webrtc.v1.point.enums.PointMessage.EXTENSION;
import static webrtc.v1.point.enums.PointUnit.CREATE_CHANNEL;
import static webrtc.v1.point.enums.PointUnit.EXTENSION_CHANNEL_POINT;

import io.azam.ulidj.ULID;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import webrtc.v1.user.entity.Users;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Point implements Serializable {

  @Id
  @Builder.Default
  private String id = ULID.random();

  private String message;
  private int amount;

  @Builder.Default
  private Timestamp created_at = new Timestamp(System.currentTimeMillis());

  @ManyToOne
  @JoinColumn(name = "USER_ID")
  private Users user;

  public static Point extensionChannelTTL(final String email, final Long ttl) {
    return Point.builder()
        .message(email + EXTENSION.getMessage())
        .amount(-(int) (ttl * EXTENSION_CHANNEL_POINT.getUnit()))
        .build();
  }

  public static Point createChannel(final String email) {
    return Point.builder()
        .message(email + CREATE.getMessage())
        .amount(-CREATE_CHANNEL.getUnit())
        .build();
  }

  public static Point welcomePoint() {
    return Point.builder()
        .message("1234")
        .amount(10000000)
        .build();
  }
  public void setUser(Users user) {
    this.user = user;
  }
}
