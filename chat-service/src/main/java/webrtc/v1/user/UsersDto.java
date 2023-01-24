package webrtc.v1.user;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UsersDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CreateUserRequest {
    private String nickname;
    private String password;
    private String email;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FindUserByEmailRequest {
    private String email;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DecreasePointRequest {
    private String userEmail;
    private int point;
    private String message;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class JwtRequest implements Serializable {
    private static final Long serialVersionUID = 5926468583005150707L;

    private String email;
    private String password;
  }


  public static class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;

    public JwtResponse(String jwttoken) {
      this.jwttoken = jwttoken;
    }

    public String getJwttoken() {
      return this.jwttoken;
    }
  }
}