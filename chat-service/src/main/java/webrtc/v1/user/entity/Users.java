package webrtc.v1.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import io.azam.ulidj.ULID;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.v1.channel.entity.ChannelUser;
import webrtc.v1.point.entity.Point;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users implements Serializable {

  @Id
  @Builder.Default
  @NotNull
  private String id = ULID.random();
  private String email;


  @Builder.Default
  private Timestamp created_at = new Timestamp(System.currentTimeMillis());
  @Builder.Default
  private Timestamp updated_at = new Timestamp(System.currentTimeMillis());
  @JsonIgnore
  private String password;

  @Builder.Default
  private Date birthdate = new Date(1997, 12, 20);
  @Builder.Default
  private String phone_number = "010-4902-5037";
  @Builder.Default
  private String school = "부산";
  @Builder.Default
  private String company = "백수";
  private String nickname;
  @Builder.Default
  private Timestamp nickname_expire_at = new Timestamp(System.currentTimeMillis());
  @Builder.Default
  @JsonIgnore
  private Boolean is_admin = false;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonIgnore
  @Builder.Default
  private List<Point> points = new ArrayList<>();

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  @Builder.Default
  private List<ChannelUser> channelUsers = new ArrayList<>();

  public void addPoint(Point point) {
    this.points.add(point);
    point.setUser(this);
  }

  public int sumOfPoint() {
    return points.stream()
        .map(Point::getAmount)
        .reduce(0, Integer::sum);
  }

  public void addChannelUser(ChannelUser channelUser) {
    this.channelUsers.add(channelUser);
  }

  public void exitChannelUser(ChannelUser channelUser) {
    this.channelUsers.remove(channelUser);
  }
}
