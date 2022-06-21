package webrtc.authservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class User implements Serializable {

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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id")
    @JsonIgnore
    private Point point;

    public User(String nickname, String password, String email) {
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

        Point point = new Point();
        point.setPoint(1000000L);
        this.point = point;
    }
}
