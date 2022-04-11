package webrtc.openvidu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

public class JwtDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JwtRequest implements Serializable {
        private static final Long serialVersionUID = 5926468583005150707L;
        private String username;
        private String password;
    }

    public static class JwtResponse implements Serializable {
        private static final Long serialVersionUID = -8091879091924046844L;
        private final String jwtToken;

        public JwtResponse(String jwtToken) {
            this.jwtToken = jwtToken;
        }

        public String getJwtToken() {
            return this.jwtToken;
        }
    }
}
