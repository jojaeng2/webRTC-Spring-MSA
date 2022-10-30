package webrtc.authservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Point implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String message;
    private int amount;

    @Builder.Default
    private Timestamp created_at = new Timestamp(System.currentTimeMillis());

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    public void setUser(Users user) {
        this.user = user;
    }

}
