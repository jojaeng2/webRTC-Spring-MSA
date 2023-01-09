package webrtc.v1.utils.response;

import lombok.Getter;

@Getter
public class HttpStatusResponse {

  private final String code;
  private final String message;

  public HttpStatusResponse(String code, String message) {
    this.code = code;
    this.message = message;
  }
}