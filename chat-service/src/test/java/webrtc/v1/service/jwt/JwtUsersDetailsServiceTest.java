package webrtc.v1.service.jwt;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.user.entity.Users;
import webrtc.v1.enums.ChannelType;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.user.repository.UsersRepository;
import webrtc.v1.utils.jwt.JwtUserDetailsService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static webrtc.v1.enums.ChannelType.TEXT;
import static webrtc.v1.enums.ChannelType.VOIP;

@ExtendWith(MockitoExtension.class)
public class JwtUsersDetailsServiceTest {

    @InjectMocks
    private JwtUserDetailsService jwtUserDetailsService;

    @Mock
    private UsersRepository usersRepository;

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
        Users user = new Users(nickname1, password, email1);
        doReturn(Optional.of(user))
                .when(usersRepository).findByEmail(any(String.class));

        // when
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(email1);

        // then
        Assertions.assertThat(userDetails.getUsername()).isEqualTo(email1);
    }

    @Test
    @Transactional
    public void 유저이름으로_UserDetails_조회실패() {
        // given

        // when

        // then
        assertThrows(NotExistUserException.class, ()-> {
            jwtUserDetailsService.loadUserByUsername(email1);
        });
    }
}
