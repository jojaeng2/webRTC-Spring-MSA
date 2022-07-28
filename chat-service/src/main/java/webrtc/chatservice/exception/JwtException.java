package webrtc.chatservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import webrtc.chatservice.enums.SocketInterceptorErrorType;

public class JwtException {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomJwtExceptionDto {
        private SocketInterceptorErrorType type;
        private String exception;

        public void setField(SocketInterceptorErrorType type, String exception) {
            this.type = type;
            this.exception = exception;
        }
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public static class JwtAccessTokenNotValid extends RuntimeException {

    }
}
