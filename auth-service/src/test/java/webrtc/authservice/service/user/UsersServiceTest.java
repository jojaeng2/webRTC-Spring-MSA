package webrtc.authservice.service.user;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.authservice.domain.Point;
import webrtc.authservice.domain.Users;
import webrtc.authservice.dto.UserDto.CreateUserRequest;
import webrtc.authservice.dto.UserDto.FindUserWithPointByEmailResponse;
import webrtc.authservice.exception.UserException;
import webrtc.authservice.exception.UserException.InsufficientPointException;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UsersServiceTest {

    @Autowired
    private UserService userService;
    private String nickname = "nickname";
    private String password = "password";
    private String email = "email";
    private int welcomePoint = 1000000;

    @BeforeEach
    public void Reset() {
        CreateUserRequest request = new CreateUserRequest(nickname, password, email);
        userService.save(request);
        userService.redisDataEvict();
    }

    @Test
    @Transactional
    public void 유저_회원가입() {
        // given

        // when
        Users findUser = userService.findOneUserByEmail(email);

        // then
        assertThat(findUser.getEmail()).isEqualTo(email);
        assertThat(findUser.getNickname()).isEqualTo(nickname);
    }

    @Test
    @Transactional
    public void Email로_유저찾기_성공() {
        // given

        // when
        Users findUser = userService.findOneUserByEmail(email);

        // then
        assertThat(findUser.getEmail()).isEqualTo(email);
        assertThat(findUser.getNickname()).isEqualTo(nickname);
    }

    @Test
    @Transactional
    public void Email로_유저찾기_실패() {
        // given

        // when

        // then
        assertThrows(UserException.NotExistUserException.class, () -> userService.findOneUserByEmail("not exist email"));
    }

    @Test
    @Transactional
    public void Email로_유저포인트정보와찾기_성공() {
        // given
        Users user = userService.findOneUserByEmail(email);
        int num = 10;
        int amount = 100;
        for(int i=0; i<num; i++) {
            Point point = new Point("포인트 저장", amount);
            user.addPoint(point);
        }

        // when
        FindUserWithPointByEmailResponse response = userService.findOneUserWithPointByEmail(email);

        // then
        assertThat(response.getPoint()-welcomePoint).isEqualTo(num * amount);
    }

    @Test
    @Transactional
    public void 유저포인트_감소_성공() {
        // given
        Users user = userService.findOneUserByEmail(email);

        // when
        userService.decreasePoint(user.getEmail(), welcomePoint/10);
        FindUserWithPointByEmailResponse response = userService.findOneUserWithPointByEmail(email);

        // then
        assertThat(response.getPoint()).isEqualTo(welcomePoint - welcomePoint/10);

    }

    @Test
    @Transactional
    public void 유저포인트_감소_실패() {
        // given
        Users user = userService.findOneUserByEmail(email);

        // when


        // then
        Assertions.assertThrows(InsufficientPointException.class, ()-> userService.decreasePoint(user.getEmail(), welcomePoint*10));

    }

}
