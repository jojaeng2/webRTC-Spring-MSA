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
import webrtc.v1.staticgenarator.UserGenerator;
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
  private UsersRepository usersRepository;

  String nickname1 = "nickname1";
  String password = "password";


  @Test
  public void 이메일로_유저찾기_성공() {
    //given

    Users user = UserGenerator.createUsers();

    //when
    Users findUsers = userService.findOneById(user.getId());

    //then
    assertThat(user.getEmail()).isEqualTo(findUsers.getEmail());
  }
}
