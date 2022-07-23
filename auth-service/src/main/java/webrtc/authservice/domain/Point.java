package webrtc.authservice.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Point implements Serializable {

    @Id
    @Column(name = "point_id")
    private String id;

    private String message;
    private int amount;
    private Timestamp created_at;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public Point(String message, int amount) {
        this.id = UUID.randomUUID().toString();
        this.message = message;
        this.amount = amount;
        this.created_at = new Timestamp(System.currentTimeMillis());
    }
}
