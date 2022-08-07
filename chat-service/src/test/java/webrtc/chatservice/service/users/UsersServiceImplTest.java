package webrtc.chatservice.service.users;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.ChannelDto.ExtensionChannelInfoWithUserPointResponse;
import webrtc.chatservice.dto.UserDto.CreateUserRequest;
import webrtc.chatservice.dto.UserDto.FindUserWithPointByEmailResponse;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.channel.ChannelRedisRepository;
import webrtc.chatservice.repository.users.UsersRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsersServiceImplTest {

    @InjectMocks
    private UsersServiceImpl userService;
    @Mock
    private ChannelRedisRepository channelRedisRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private HttpApiController httpApiController;
    @Mock
    private BCryptPasswordEncoder encoder;

    String nickname1 = "nickname1";
    String password = "password";
    String email1 = "email1";
    @BeforeEach
    public void clearUserCache() {
        userService.redisDataEvict();
    }

    @Test
    public void 유저저장_성공() {
        // given

        CreateUserRequest request = new CreateUserRequest(nickname1, password, email1);

        doReturn(password)
                .when(encoder).encode(any(String.class));
        doNothing()
                .when(usersRepository)
                .saveUser(any(User.class));

        // when
        User response = userService.saveUser(request);

        // then
        assertThat(request.getEmail()).isEqualTo(response.getEmail());
    }


    @Test
    public void 이메일로_유저찾기_성공() {
        //given
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String email = "email";

        doReturn(new User(nickname1, password, email))
                .when(usersRepository).findUserByEmail(email);

        //when
        User user = userService.findOneUserByEmail(email);

        //then
        assertThat(user.getEmail()).isEqualTo(email);
    }


    @Test
    @Transactional
    public void 이메일로_유저찾기_실패후_통신성공() {
        //given
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String email = "email";

        doReturn(new User(nickname1, password, email))
                .when(httpApiController).postFindUserByEmail(email);

        doThrow(new NotExistUserException())
                .when(usersRepository).findUserByEmail(email);

        //when
        User user = userService.findOneUserByEmail(email);

        //then
        assertThat(user.getEmail()).isEqualTo(email);
    }

    @Test
    @Transactional
    public void 이메일로_유저찾기_실패후_통신실패() {
        //given
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        doThrow(new NotExistUserException())
                .when(httpApiController).postFindUserByEmail(email1);


        doThrow(new NotExistUserException())
                .when(usersRepository).findUserByEmail(email1);

        //when

        //then
        assertThrows(NotExistUserException.class, ()-> {
            userService.findOneUserByEmail(email1);
        });
    }

    @Test
    @Transactional
    public void 이메일로_유저정보와포인트_반환성공() {
        // given
        String id = "id";
        String email = "email";
        String channelId = "channelId";
        String nickname = "nickname";
        int point = 10000000;

        Long channelTTL = 100000L;

        doReturn(new FindUserWithPointByEmailResponse(id, email, nickname, point))
                .when(httpApiController).postFindUserWithPointByEmail(email);
        doReturn(channelTTL)
                .when(channelRedisRepository).findChannelTTL(channelId);

        // when
        ExtensionChannelInfoWithUserPointResponse response = userService.findUserWithPointByEmail(channelId, email);

        // then
        assertThat(response.getPoint()).isEqualTo(point);
        assertThat(response.getChannelTTL()).isEqualTo(channelTTL);
    }

    @Test
    @Transactional
    public void 이메일로_유저정보와포인트_반환실패() {
        // given
        String id = "id";
        String email = "email";
        String channelId = "channelId";
        String nickname = "nickname";
        int point = 10000000;

        Long channelTTL = 100000L;

        doThrow(new NotExistUserException())
                .when(httpApiController).postFindUserWithPointByEmail(email);

        // when

        // then
        Assertions.assertThrows(NotExistUserException.class, ()-> {
            userService.findUserWithPointByEmail(channelId, email);
        });
    }
}
