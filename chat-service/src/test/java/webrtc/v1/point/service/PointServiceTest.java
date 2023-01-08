package webrtc.v1.point.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import webrtc.v1.point.repository.PointRepository;
import webrtc.v1.staticgenarator.UserGenerator;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.user.repository.UsersRepository;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

  @InjectMocks
  private PointServiceImpl pointService;

  @Mock
  private PointRepository pointRepository;

  @Mock
  private UsersRepository userRepository;

  @Test
  void findPointSum성공() {
    // given
    Users user = UserGenerator.createUsers();

    doReturn(Optional.of(user))
        .when(userRepository).findById(any(String.class));
    doReturn(user.getPoints())
        .when(pointRepository).findByUser(any(Users.class));
    // when
    int sum = pointService.findPointSum(user.getId());

    // then
    assertThat(sum).isEqualTo(1000000);
  }

  @Test
  void findPointSum실패_유저없음() {
    // given
    Users user = UserGenerator.createUsers();

    doThrow(NotExistUserException.class)
        .when(userRepository).findById(any(String.class));

    // when

    // then
    org.junit.jupiter.api.Assertions.assertThrows(NotExistUserException.class,
        () -> pointService.findPointSum(user.getId()));
  }
}
