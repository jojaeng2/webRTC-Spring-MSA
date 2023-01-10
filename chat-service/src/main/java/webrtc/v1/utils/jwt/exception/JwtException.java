package webrtc.v1.utils.jwt.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import webrtc.v1.chat.enums.SocketInterceptorErrorType;

public class JwtException {

  @AllArgsConstructor
  public static class CustomJwtExceptionDto {
    private SocketInterceptorErrorType type;
    private String exception;
  }

  @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
  public static class JwtAccessTokenNotValid extends RuntimeException {

  }
}
