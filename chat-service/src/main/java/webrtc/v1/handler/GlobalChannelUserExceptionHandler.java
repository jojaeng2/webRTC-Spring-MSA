package webrtc.v1.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import webrtc.v1.channel.exception.ChannelUserException.NotExistChannelUserException;
import webrtc.v1.utils.response.HttpStatusResponse;

@RestControllerAdvice
public class GlobalChannelUserExceptionHandler {

    @ExceptionHandler(NotExistChannelUserException.class)
    protected ResponseEntity<?> handleNotExistChannelUserException(NotExistChannelUserException e) {
        final HttpStatusResponse httpStatusResponse = new HttpStatusResponse("해당 채널에 존재하지 않는 사용자입니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, HttpStatus.NOT_FOUND);
    }
}
