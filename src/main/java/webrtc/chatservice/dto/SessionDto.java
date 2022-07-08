package webrtc.chatservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class SessionDto {

    @Getter
    public static class GetTokenRequest {
        private String sessionName;
    }

    @Getter
    @AllArgsConstructor
    public static class GetTokenResponse {

        private String token;


    }

}
