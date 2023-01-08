package webrtc.v1.point.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import webrtc.v1.point.entity.Point;
import webrtc.v1.point.repository.PointRepository;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.repository.UsersRepository;

import java.util.List;

@DataJpaTest
public class PointRepositoryTest {

    @Autowired
    private PointRepository repository;
    @Autowired
    private UsersRepository usersRepository;

    private final String email = "email";
    private final String nickname = "nickname";
    private final String password = "password";

    @Test
    void 회원포인트조회() {
        // given
        Users user = createUser();
        Point point1 = createPoint();
        Point point2 = createPoint();

        user.addPoint(point1);
        user.addPoint(point2);

        // when
        usersRepository.save(user);
        // then
        List<Point> byUser = repository.findByUser(user);
        for (Point point : byUser) {
            System.out.println("point = " + point.getId());
        }
    }

    Users createUser() {
        return Users.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .build();
    }

    Point createPoint() {
        return Point.builder()
                .message("test")
                .amount(1000)
                .build();
    }
}
