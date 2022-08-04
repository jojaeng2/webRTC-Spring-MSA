package webrtc.authservice.repository.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        Users findUser = userRepository.findUserByEmail(email);

        // then
        Assertions.assertThat(user).isEqualTo(findUser);
    }

    @Test
    public void Email로_유저_찾기() {
        // given

        Users user = new Users(nickname, password, email);

        userRepository.save(user);

        // when
        Users findUser = userRepository.findUserByEmail(email);

        // then
        Assertions.assertThat(user).isEqualTo(findUser);
    }

    @Test
    public void 유저_포인트_추가() {
        // given
        Users user = new Users(nickname, password, email);
        Point point = new Point("회원 가입", 100);

        // when
        user.addPoint(point);
        userRepository.save(user);
        Users findUser = userRepository.findUserByEmail(email);
        List<Point> pointList = findUser.getPoints();

        // then
        Assertions.assertThat(point).isEqualTo(pointList.get(0));
    }
    
    @Test
    public void 유저_포인트_조회() {
        // given
        Users user = new Users(nickname, password, email);
        Point point1 = new Point("회원 가입", 100);
        Point point2 = new Point("채널 입장", 100);
        user.addPoint(point1);
        user.addPoint(point2);

        // when
        userRepository.save(user);
        Users findUser = userRepository.findUserByEmail(email);
        List<Point> pointList = findUser.getPoints();

        // then
        Assertions.assertThat(pointList.size()).isEqualTo(2);
    }
}
