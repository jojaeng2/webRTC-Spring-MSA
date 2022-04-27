package webrtc.openvidu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ChannelException {

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Success")
    public static class AlreadyExistChannelException extends RuntimeException {
        private static final String MESSAGE = "채널에 입장합니다.";

        public AlreadyExistChannelException() {
            super(MESSAGE);
        }
    }
}
