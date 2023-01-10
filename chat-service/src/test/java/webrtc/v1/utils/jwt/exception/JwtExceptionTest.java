package webrtc.v1.utils.jwt.exception;

import org.junit.jupiter.api.Test;
import webrtc.v1.chat.enums.SocketInterceptorErrorType;
import webrtc.v1.utils.jwt.exception.JwtException.CustomJwtExceptionDto;

public class JwtExceptionTest {

  @Test
  void JwtException생성() {
    // given
    JwtException jwtException = new JwtException();
    // when

    // then
  }

  @Test
  void CustomJwtExceptionDto생성() {
    // given
    CustomJwtExceptionDto customJwtExceptionDto = new CustomJwtExceptionDto(
        SocketInterceptorErrorType.INTERNAL_ERROR, "Server Error 500");

    // when

    // then
  }
}
