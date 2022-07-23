package webrtc.authservice.service.user;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.authservice.domain.User;
import webrtc.authservice.dto.UserDto.CreateUserRequest;
import webrtc.authservice.exception.UserException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;
    private String nickname = "nickname";
    private String password = "password";
    private String email = "email";

    @BeforeEach
    public void redisReset() {
        CreateUserRequest request = new CreateUserRequest(nickname, password, email);
        userService.save(request);
        userService.redisDataEvict();
    }

    @Test
    public void 유저_회원가입() {
        // given

        // when
        User findUser = userService.findOneUserByEmail(email);

        // then
        Assertions.assertThat(findUser.getEmail()).isEqualTo(email);
        Assertions.assertThat(findUser.getNickname()).isEqualTo(nickname);
    }

    @Test
    public void Email로_유저찾기_성공() {
        // given

        // when
        User findUser = userService.findOneUserByEmail(email);

        // then
        Assertions.assertThat(findUser.getEmail()).isEqualTo(email);
        Assertions.assertThat(findUser.getNickname()).isEqualTo(nickname);
    }

    @Test
    public void Email로_유저찾기_실패() {
        // given

        // when

        // then
        assertThrows(UserException.NotExistUserException.class, () -> userService.findOneUserByEmail("not exist email"));
    }
}
