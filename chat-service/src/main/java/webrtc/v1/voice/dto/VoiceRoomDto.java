package webrtc.v1.voice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class VoiceRoomDto {

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
