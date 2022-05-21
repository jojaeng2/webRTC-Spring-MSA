package webrtc.openvidu.domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class Point {

    @Id
    @GeneratedValue
    @Column(name = "point_id")
    private Long id;

    private Long point;


}
