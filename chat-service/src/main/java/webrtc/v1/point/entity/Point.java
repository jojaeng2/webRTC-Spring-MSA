package webrtc.v1.point.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.v1.point.enums.PointMessage;
import webrtc.v1.user.entity.Users;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

import static webrtc.v1.point.enums.PointMessage.CREATE;
import static webrtc.v1.point.enums.PointMessage.EXTENSION;
import static webrtc.v1.point.enums.WelcomePoint.JOIN;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Point implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private int amount;

    @Builder.Default
    private Timestamp created_at = new Timestamp(System.currentTimeMillis());

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    public static Point extensionChannelTTL(String email, Long ttl) {
        final long pointUnit = 100L;

        return Point.builder()
                .message(email + EXTENSION.getMessage())
                .amount(-(int) (ttl * pointUnit))
                .build();
    }

    public static Point createChannel(String email) {

        final long pointUnit = 100L;
        final long channelCreatePoint = 2L;
        return Point.builder()
                .message(email + CREATE.getMessage())
                .amount(-(int) (channelCreatePoint * pointUnit))
                .build();
    }

    public static Point welcomePoint() {
        return Point.builder()
                .message(JOIN.getMessage())
                .amount(JOIN.getPoint())
                .build();
    }

    public void setUser(Users user) {
        this.user = user;
    }

}
