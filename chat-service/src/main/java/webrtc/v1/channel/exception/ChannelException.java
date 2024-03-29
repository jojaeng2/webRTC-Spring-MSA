package webrtc.v1.channel.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import webrtc.v1.chat.enums.SocketInterceptorErrorType;

public class ChannelException extends RuntimeException {

  @AllArgsConstructor
  public static class ChannelExceptionDto {
    private SocketInterceptorErrorType type;
    private String exception;
    private int idx;
  }

  @ResponseStatus(code = HttpStatus.CONFLICT)
  public static class AlreadyExistChannelException extends ChannelException {

  }

  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  public static class NotExistChannelException extends ChannelException {

  }

  @ResponseStatus(code = HttpStatus.ALREADY_REPORTED)
  public static class AlreadyExistUserInChannelException extends ChannelException {

  }

  @ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
  public static class ChannelParticipantsFullException extends ChannelException {

  }
}
