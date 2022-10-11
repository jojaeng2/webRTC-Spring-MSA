package webrtc.authservice.repository.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import webrtc.authservice.domain.Point;
import webrtc.authservice.domain.Users;

import java.util.List;

@SpringBootTest
@Transactional
public class UsersRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private String nickname = "nickname";
    private String password = "password";
    private String email = "email";

    @Test
    public void 유저_저장_성공() {
        // given
        Users user = new Users(nickname, password, email);

        // when
        userRepository.save(user);
        Users findUser = userRepository.findUserByEmail(email).get();

        // then
        Assertions.assertThat(user).isEqualTo(findUser);
    }

    @Test
    public void Email로_유저_찾기() {
        // given

        Users user = new Users(nickname, password, email);

        userRepository.save(user);

        // when
        Users findUser = userRepository.findUserByEmail(email).get();

        // then
        Assertions.assertThat(user).isEqualTo(findUser);
    }

    @Test
    public void 유저_포인트_추가() {
        // given
        Users user = new Users(nickname, password, email);
        Point point = Point.builder()
                .message("회원 가입")
                .amount(100)
                .build();

        // when
        user.addPoint(point);
        userRepository.save(user);
        Users findUser = userRepository.findUserByEmail(email).get();
        List<Point> pointList = findUser.getPoints();

        // then
        Assertions.assertThat(point).isEqualTo(pointList.get(0));
    }
    
    @Test
    public void 유저_포인트_조회() {
        // given
        Users user = new Users(nickname, password, email);
        Point point1 = Point.builder()
                .message("회원 가입")
                .amount(100)
                .build();
        Point point2 = Point.builder()
                .message("채널 입장")
                .amount(100)
                .build();
        user.addPoint(point1);
        user.addPoint(point2);

        // when
        userRepository.save(user);
        Users findUser = userRepository.findUserByEmail(email).get();
        List<Point> pointList = findUser.getPoints();

        // then
        Assertions.assertThat(pointList.size()).isEqualTo(2);
    }
}
