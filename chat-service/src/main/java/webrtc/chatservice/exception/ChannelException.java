package webrtc.chatservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import webrtc.chatservice.enums.SocketInterceptorErrorType;

public class ChannelException extends RuntimeException{

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChannelExceptionDto {

        private SocketInterceptorErrorType type;
        private String exception;
        private Long idx;

        public void setField(SocketInterceptorErrorType type, String exception, Long idx) {
            this.type = type;
            this.exception = exception;
            this.idx = idx;
        }

        public ChannelExceptionDto(SocketInterceptorErrorType type, String exception) {
            this.type = type;
            this.exception = exception;
        }
    }

    @ResponseStatus(code = HttpStatus.CONFLICT)
    public static class AlreadyExistChannelException extends ChannelException {

    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public static class NotExistChannelException extends ChannelException {

    }

    @ResponseStatus(code = HttpStatus.ALREADY_REPORTED)
    public static class AlreadyExistUserInChannelException extends ChannelException {
        private Long idx;

        public void setIdx(Long idx) {
            this.idx = idx;
        }
    }

    @ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
    public static class ChannelParticipantsFullException extends ChannelException {

    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public static class NotExistEnterChannelException extends ChannelException {

    }
}
