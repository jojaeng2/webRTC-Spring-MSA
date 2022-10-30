package webrtc.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.authservice.domain.Users;

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
    public static class FindUserWithPointByEmailResponse {
        private String id;
        private String email;
        private String nickname;
        private int point;

        public FindUserWithPointByEmailResponse(Users user) {
            this.id = user.getId().toString();
            this.email = user.getEmail();
            this.nickname = user.getNickname();
            this.point = user.sumOfPoint(user.getPoints());
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DecreasePointRequest {
        private String userEmail;
        private int point;
        private String message;
    }
}
