package webrtc.authservice.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor
public class Point implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "point_id")
    private Long id;

    private Long point;

    @OneToOne(mappedBy = "point")
    private User user;

    public void setPoint(Long point) {
        this.point = point;
    }
}
