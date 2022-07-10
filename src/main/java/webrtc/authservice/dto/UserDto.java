package webrtc.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDto {

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
    public static class FindUserWithPointByEmailRequest {
        private String email;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindUserWithPointByEmailResponse {
        private String id;
        private String email;
        private String nickname;
        private Long point;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DecreasePointRequest {
        private String userEmail;
        private Long point;
    }
}
