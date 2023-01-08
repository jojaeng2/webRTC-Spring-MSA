package webrtc.v1.staticgenarator;

import webrtc.v1.point.entity.Point;
import webrtc.v1.user.entity.Users;

public class PointGenerator {

  private static final String message = "회원가입";
  private static final int amount = 1000000;

  private PointGenerator() {
  }

  public static Point createPoint() {
    return Point.builder()
        .message(message)
        .amount(amount)
        .build();
  }

  public static String getMessage() {
    return message;
  }
}
