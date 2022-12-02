package webrtc.v1.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.v1.channel.entity.ChannelUser;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users implements Serializable {

    @Id
    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    @Builder.Default
    private UUID id = UUID.randomUUID();
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Point> points = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    @Builder.Default
    private List<ChannelUser> channelUsers = new ArrayList<>();

    public Users(String nickname, String password, String email) {
        this.nickname = nickname;
        this.password = password;
        this.email = email;

        // 임시
        this.nickname_expire_at = new Timestamp(System.currentTimeMillis());
    }

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
