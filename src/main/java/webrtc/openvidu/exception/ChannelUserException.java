package webrtc.openvidu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ChannelUserException {

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public static class NotExistChannelUserException extends RuntimeException {

    }
}
