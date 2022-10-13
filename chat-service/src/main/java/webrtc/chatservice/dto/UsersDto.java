package webrtc.chatservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;

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
    public static class FindUserWithPointByEmailResponse {
        private String id;
        private String email;
        private String nickname;
        private int point;
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
