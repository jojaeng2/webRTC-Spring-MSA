package webrtc.v1.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import webrtc.v1.staticgenarator.UserGenerator;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.user.repository.UsersRepository;
import webrtc.v1.user.service.UsersServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UsersServiceImplTest {

  @InjectMocks
  private UsersServiceImpl userService;
  @Mock
  private UsersRepository usersRepository;

  @Test
  public void ID로_유저찾기_성공() {
    //given

    Users user = UserGenerator.createUsers();
    doReturn(Optional.of(user))
        .when(usersRepository).findById(any(String.class));

    //when
    Users findUsers = userService.findOneById(user.getId());

    //then
    assertThat(user.getEmail()).isEqualTo(findUsers.getEmail());
  }

  @Test
  public void ID로_유저찾기실패() {
    //given

    Users user = UserGenerator.createUsers();
    doThrow(NotExistUserException.class)
        .when(usersRepository).findById(any(String.class));

    //when

    //then
    assertThrows(NotExistUserException.class, () -> userService.findOneById(user.getId()));
  }
}
