package webrtc.authservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
public class Users implements Serializable {

    @Id
    @Column(name = "user_id")
    private String id;
    private Timestamp created_at;
    private Timestamp updated_at;
    private String email;
    private String password;
    private Date birthdate;
    private String phone_number;
    private String school;
    private String company;
    private String nickname;
    private Timestamp nickname_expire_at;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Point> points;

    public Users(String nickname, String password, String email) {
        this.id = UUID.randomUUID().toString();
        this.nickname = nickname;
        this.password = password;
        this.email = email;

        // 임시
        this.created_at = null;
        this.updated_at = null;
        this.birthdate = null;
        this.phone_number = null;
        this.school = null;
        this.company = null;
        this.nickname_expire_at = null;
        this.points = new ArrayList<>();
    }

    public void addPoint(Point point) {
        this.points.add(point);
        point.setUser(this);
    }

    public int sumOfPoint(List<Point> points) {
        int sum = 0;
        for (Point point : points) sum += point.getAmount();
        return sum;
    }
}
