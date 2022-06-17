package webrtc.chatservice.service.jwt;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.dto.UserDto.CreateUserRequest;
import webrtc.chatservice.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class JwtUserDetailsServiceTest {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private UserService userService;

    @Test
    @DisplayName("userName으로 UserDetails 조회 성공")
    public void findUserDetailsByUsernameSuccess() {
        // given
        CreateUserRequest createUserRequest = new CreateUserRequest("user", "user", "email");
        userService.saveUser(createUserRequest);

        // when
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername("email");

        // then
        Assertions.assertThat(userDetails.getUsername()).isEqualTo("email");
    }

    @Test
    @DisplayName("userName으로 userDetails 조회 실패")
    public void findUserDetailsByUsernameFail() {
        // given
        CreateUserRequest createUserRequest = new CreateUserRequest("user", "user", "email");
        userService.saveUser(createUserRequest);

        // when

        // then
        assertThrows(UsernameNotFoundException.class,
                ()-> jwtUserDetailsService.loadUserByUsername("NotExistUserName"));
    }
}
