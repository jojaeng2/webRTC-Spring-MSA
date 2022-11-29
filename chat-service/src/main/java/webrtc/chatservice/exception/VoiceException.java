package webrtc.chatservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class VoiceException {
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public static class OpenViduClientException extends RuntimeException {

    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public static class NotExistOpenViduServerException extends RuntimeException {

    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public static class AlreadyRemovedSessionInOpenViduServer extends RuntimeException {

    }


    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public static class InvalidAccessToOpenViduServerException extends RuntimeException {

    }

    @Getter
    @NoArgsConstructor
    public static class ExceptionHttpStatusResponse {

        private String code;
        private String message;

        public ExceptionHttpStatusResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }

}
