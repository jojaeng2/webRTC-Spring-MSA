package webrtc.v1.utils.jwt.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.staticgenarator.UserGenerator;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.user.repository.UsersRepository;

@ExtendWith(MockitoExtension.class)
public class JwtUsersDetailsServiceTest {

  @InjectMocks
  private JwtUserDetailsService jwtUserDetailsService;

  @Mock
  private UsersRepository usersRepository;

  @Test
  @Transactional
  public void 유저이름으로_UserDetails_조회성공() {
    // given
    Users user = UserGenerator.createUsers();

    doReturn(Optional.of(user))
        .when(usersRepository).findById(any(String.class));

    // when
    UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(user.getId());

    // then
    Assertions.assertThat(userDetails.getUsername()).isEqualTo(user.getId());
  }

  @Test
  @Transactional
  public void 유저이름으로_UserDetails_조회실패() {
    // given
    Users user = UserGenerator.createUsers();
    doReturn(Optional.empty())
        .when(usersRepository).findById(any(String.class));
    // when

    // then
    assertThrows(NotExistUserException.class, () -> {
      jwtUserDetailsService.loadUserByUsername(user.getId());
    });
  }
}
