package webrtc.v1.utils.jwt.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import webrtc.v1.staticgenarator.UserGenerator;
import webrtc.v1.user.entity.Users;

@ExtendWith(MockitoExtension.class)
public class JwtTokenUtilTest {

  private JwtTokenUtilImpl jwtTokenUtil = new JwtTokenUtilImpl();

  @Mock
  private JwtUserDetailsService jwtUserDetailsService;

  private String jwtAccessTokenBase;

  @BeforeEach
  public void setup() {
    Users user = UserGenerator.createUsers();
    // jwt token 생성
    doReturn(
        new org.springframework.security.core.userdetails.User(user.getId(), user.getPassword(), new ArrayList<>()))
        .when(jwtUserDetailsService).loadUserByUsername(any(String.class));

    jwtAccessTokenBase = jwtTokenUtil.generateToken(jwtUserDetailsService.loadUserByUsername(user.getId()));
  }

  @Test
  void validateToken성공() {
    // given
    Users user = UserGenerator.createUsers();

    doReturn(
        new org.springframework.security.core.userdetails.User(user.getId(), user.getPassword(), new ArrayList<>()))
        .when(jwtUserDetailsService).loadUserByUsername(any(String.class));

    String jwtAccessToken = jwtTokenUtil.generateToken(jwtUserDetailsService.loadUserByUsername(user.getId()));

    // when

    // then
    Assertions.assertThat(jwtTokenUtil.isTokenExpired(jwtAccessToken)).isFalse();
  }

  @Test
  void 올바른토큰() {
    // given
    Users user = UserGenerator.createUsers();

    doReturn(
        new org.springframework.security.core.userdetails.User(user.getId(), user.getPassword(), new ArrayList<>()))
        .when(jwtUserDetailsService).loadUserByUsername(any(String.class));

    String jwtAccessToken = jwtTokenUtil.generateToken(jwtUserDetailsService.loadUserByUsername(user.getId()));
    UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(user.getId());

    // when

    // then
    Assertions.assertThat(jwtTokenUtil.validateToken(jwtAccessToken, userDetails)).isTrue();
  }

  @Test
  void 올바르지않은토큰() {
    // given
    Users user = UserGenerator.createUsers();

    doReturn(
        new org.springframework.security.core.userdetails.User(user.getId(), user.getPassword(), new ArrayList<>()))
        .when(jwtUserDetailsService).loadUserByUsername(any(String.class));

    UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(user.getId());

    // when

    // then
    Assertions.assertThat(jwtTokenUtil.validateToken(jwtAccessTokenBase, userDetails)).isFalse();
  }
}
