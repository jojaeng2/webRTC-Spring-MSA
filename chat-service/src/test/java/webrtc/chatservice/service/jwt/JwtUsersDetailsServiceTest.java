package webrtc.chatservice.service.jwt;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.service.users.UsersService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ChannelType.VOIP;

@ExtendWith(MockitoExtension.class)
public class JwtUsersDetailsServiceTest {

    @InjectMocks
    private JwtUserDetailsService jwtUserDetailsService;
    @Mock
    private HttpApiController httpApiController;

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

    @Test
    @Transactional
    public void 유저이름으로_UserDetails_조회성공() {
        // given

        doReturn(new Users(nickname1, password, email1))
                .when(httpApiController).postFindUserByEmail(email1);

        // when
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(email1);

        // then
        Assertions.assertThat(userDetails.getUsername()).isEqualTo(email1);
    }

    @Test
    @Transactional
    public void 유저이름으로_UserDetails_조회실패() {
        // given
        doThrow(new NotExistUserException())
                .when(httpApiController).postFindUserByEmail(email1);

        // when

        // then
        assertThrows(NotExistUserException.class, ()-> {
            jwtUserDetailsService.loadUserByUsername(email1);
        });
    }
}
