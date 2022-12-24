package webrtc.v1.service.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.point.entity.Point;
import webrtc.v1.point.service.PointService;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.dto.UsersDto.CreateUserRequest;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.channel.repository.ChannelRedisRepository;
import webrtc.v1.user.repository.UsersRepository;
import webrtc.v1.user.service.UsersServiceImpl;

import java.util.Optional;
import java.util.UUID;

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
    private BCryptPasswordEncoder encoder;
    @Mock
    private PointService pointService;

    String nickname1 = "nickname1";
    String password = "password";
    String email1 = "email1";
    @Test
    public void 유저저장_성공() {
        // given

        CreateUserRequest request = new CreateUserRequest(nickname1, password, email1);

        doReturn(password)
                .when(encoder).encode(any(String.class));
        doReturn(Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build())
                .when(usersRepository)
                .save(any(Users.class));

        // when
        Users response = userService.save(request);

        // then
        assertThat(request.getEmail()).isEqualTo(response.getEmail());
    }


    @Test
    public void 이메일로_유저찾기_성공() {
        //given
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String email = "email";

        doReturn(Optional.of(Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email)
                .build()))
                .when(usersRepository).findById(any(String.class));

        //when
        Users users2 = userService.findOneById("1");

        //then
        assertThat(users2.getEmail()).isEqualTo(email);
    }

//    @Test
//    @Transactional
//    public void 이메일로_유저와포인트_반환성공() {
//        // given
//        int point = 10000000;
//
//        Long channelTTL = 100000L;
//        usersRepository.save(createUsers());
//        doReturn(Optional.of(createUsers()))
//                .when(usersRepository).findByEmail(any(String.class));
//
//        // when
//        int response = pointService.findPointSum(email1);
//
//        // then
//        assertThat(response).isEqualTo(point);
//    }
//
//    @Test
//    @Transactional
//    public void 이메일로_유저정보와포인트_반환실패() {
//        // given
//        String id = "id";
//        String email = "email";
//        String channelId = "channelId";
//        String nickname = "nickname";
//        int point = 10000000;
//
//        Long channelTTL = 100000L;
//
//        // when
//
//        // then
//        Assertions.assertThrows(NotExistUserException.class, ()-> {
//            pointService.findPointSum(email);
//        });
//    }

    Users createUsers() {
        Users user = Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();
        user.addPoint(createPoint());
        return user;
    }

    Point createPoint() {
        return Point.builder()
                .message("회원가입")
                .amount(10000000)
                .build();

    }
}
