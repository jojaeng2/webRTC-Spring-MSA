package webrtc.v1.voice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class VoiceException {

  private VoiceException() {
  }

  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public static class OpenViduClientException extends RuntimeException {

  }

  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  public static class AlreadyRemovedSessionInOpenViduServer extends RuntimeException {

  }


}
