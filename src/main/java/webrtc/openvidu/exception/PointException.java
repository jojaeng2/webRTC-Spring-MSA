package webrtc.openvidu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class PointException extends RuntimeException {

    @ResponseStatus(code = HttpStatus.CONFLICT)
    public static class InsufficientPointException extends PointException {

    }
}
