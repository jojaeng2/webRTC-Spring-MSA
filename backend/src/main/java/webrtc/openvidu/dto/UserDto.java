package webrtc.openvidu.dto;

import lombok.Getter;

public class UserDto {

    @Getter
    public static class CreateUserRequest {
        private String nickname;
        private String password;
    }
}
