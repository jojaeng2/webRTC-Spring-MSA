package webrtc.chatservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;

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
    public static class CreateUserResponse {
        private String id;
        private Timestamp created_at;
        private Timestamp updated_at;
        private String email;
        private String password;
        private Date birthdate;
        private String phone_number;
        private String school;
        private String company;
        private String nickname;
        private Timestamp nickname_expire_at;
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
