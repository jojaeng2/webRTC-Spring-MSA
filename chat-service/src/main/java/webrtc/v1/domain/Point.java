package webrtc.v1.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

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

    public static Point extensionChannelTTL(String email, Long ttl) {
        final long pointUnit = 100L;

        return Point.builder()
                .message(email + " 님이 채널 연장에 포인트를 사용했습니다.")
                .amount(-(int) (ttl * pointUnit))
                .build();
    }

    public static Point createChannel(String email) {

        final long pointUnit = 100L;
        final long channelCreatePoint = 2L;
        return Point.builder()
                .message(email + " 님이 채널 생성에 포인트를 사용했습니다.")
                .amount(-(int) (channelCreatePoint * pointUnit))
                .build();
    }

    public void setUser(Users user) {
        this.user = user;
    }

}
