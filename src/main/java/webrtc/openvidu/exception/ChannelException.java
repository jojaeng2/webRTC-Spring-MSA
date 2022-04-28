package webrtc.openvidu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ChannelException {

    @ResponseStatus(code = HttpStatus.CONFLICT)
    public static class AlreadyExistChannelException extends RuntimeException {

    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public static class NotExistChannelException extends RuntimeException {

    }
}
