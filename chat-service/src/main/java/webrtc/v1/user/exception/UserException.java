package webrtc.v1.user.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import webrtc.v1.chat.enums.SocketInterceptorErrorType;

public class UserException {

  private UserException() {}

  @AllArgsConstructor
  public static class NotExistUserExceptionDto {

    private SocketInterceptorErrorType type;
    private String exception;
  }

  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  public static class NotExistUserException extends RuntimeException {

  }
}
