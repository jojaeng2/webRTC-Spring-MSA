package webrtc.voiceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SessionDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetTokenRequest {
        private String sessionName;
        private String email;
    }

    @Getter
    @AllArgsConstructor
    public static class GetTokenResponse {
        private String token;
    }
}
