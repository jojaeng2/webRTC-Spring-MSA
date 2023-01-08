package webrtc.v1.staticgenarator;

import webrtc.v1.point.entity.Point;
import webrtc.v1.user.entity.Users;

public class UserGenerator {

  private static final String email = "ds4ouj@naver.com";
  private static final String password = "password";
  private static final String nickname = "jojaeng2";

  private UserGenerator() {
  }

  public static Users createUsers() {
    Users user = Users.builder()
        .email(email)
        .nickname(nickname)
        .password(password)
        .build();

    Point point = PointGenerator.createPoint();
    user.addPoint(point);
    return user;
  }


}
