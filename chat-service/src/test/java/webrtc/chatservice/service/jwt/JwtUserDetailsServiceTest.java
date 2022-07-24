package webrtc.chatservice.service.jwt;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.UserDto.CreateUserRequest;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.UserException;
import webrtc.chatservice.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ChannelType.VOIP;

@SpringBootTest
public class JwtUserDetailsServiceTest {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private UserService userService;

    String nickname1 = "nickname1";
    String nickname2 = "nickname2";
    String password = "password";
    String email1 = "email1";
    String email2 = "email2";
    String channelName1 = "channelName1";
    String notExistChannelId = "null";
    String tag1 = "tag1";
    String tag2 = "tag2";
    String tag3 = "tag3";
    ChannelType text = TEXT;
    ChannelType voip = VOIP;

    @BeforeEach
    public void clearUserCache() {
        userService.redisDataEvict();
    }


    @Test
    @Transactional
    public void 유저이름으로_UserDetails_조회성공() {
        // given
        CreateUserRequest createUserRequest = new CreateUserRequest(nickname1, password, email1);
        User user = userService.saveUser(createUserRequest);


        // when
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(email1);

        // then
        Assertions.assertThat(userDetails.getUsername()).isEqualTo(email1);
    }

    @Test
    @Transactional
    public void 유저이름으로_UserDetails_조회실패() {
        // given
        CreateUserRequest createUserRequest = new CreateUserRequest(nickname1, password, email1);
        User user = userService.saveUser(createUserRequest);

        // when

        // then
        assertThrows(ResourceAccessException.class, ()-> {
            jwtUserDetailsService.loadUserByUsername(email2);
        });
    }
}
