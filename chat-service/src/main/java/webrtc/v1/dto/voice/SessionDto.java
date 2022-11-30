package webrtc.v1.dto.voice;

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
    @NoArgsConstructor
    public static class GetTokenResponse {
        private String token;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RemoveUserInSessionRequest {
        private String sessionName;
        private String email;
        private String token;
    }
}
