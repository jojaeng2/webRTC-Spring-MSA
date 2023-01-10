package webrtc.v1.user.exception;

import static webrtc.v1.chat.enums.SocketInterceptorErrorType.NOT_FOUND_USER_BY_JWT_ACCESS_TOKEN;

import org.junit.jupiter.api.Test;
import webrtc.v1.user.exception.UserException.NotExistUserExceptionDto;

public class UserExceptionTest {

  @Test
  void NotExistUserException생성() {
    // given

    // when
    NotExistUserExceptionDto notExistUserExceptionDto = new NotExistUserExceptionDto(
        NOT_FOUND_USER_BY_JWT_ACCESS_TOKEN,
        "Jwt Access Token으로 User를 찾을 수가 없습니다.");
    // then

  }

}
