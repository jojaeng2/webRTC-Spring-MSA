package webrtc.v1.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import webrtc.v1.enums.SocketInterceptorErrorType;

public class UserException {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NotExistUserExceptionDto {

        private SocketInterceptorErrorType type;
        private String exception;

        public void setField(SocketInterceptorErrorType type, String exception) {
            this.type = type;
            this.exception = exception;
        }
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public static class NotExistUserException extends RuntimeException {

    }
}
