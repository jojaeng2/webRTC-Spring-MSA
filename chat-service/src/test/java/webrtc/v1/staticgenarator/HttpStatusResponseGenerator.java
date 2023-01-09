package webrtc.v1.staticgenarator;

import webrtc.v1.utils.response.HttpStatusResponse;

public class HttpStatusResponseGenerator {


  private static final String code = "404";
  private static final String message = "Not Found!";

  private HttpStatusResponseGenerator() {
  }

  public static HttpStatusResponse createHttpStatusResponse() {
    return new HttpStatusResponse(code, message);
  }

  public static String getCode() {
    return code;
  }

  public static String getMessage() {
    return message;
  }
}
