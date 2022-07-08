package webrtc.voiceservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UserException {

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public static class NotExistUserException extends RuntimeException {

    }
}
