package webrtc.voiceservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class SessionException {

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
}
