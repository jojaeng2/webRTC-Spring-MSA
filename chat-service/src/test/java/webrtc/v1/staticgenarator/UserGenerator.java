package webrtc.v1.staticgenarator;

import webrtc.v1.user.entity.Users;

public class UserGenerator {

  private static final String email = "ds4ouj@naver.com";
  private static final String password = "password";
  private static final String nickname = "jojaeng2";

  private UserGenerator() {}

  public static Users createUsers() {
    return Users.builder()
        .email(email)
        .nickname(nickname)
        .password(password)
        .build();
  }


}
