package webrtc.voiceservice.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import webrtc.voiceservice.dto.ExceptionHttpStatusResponse;
import webrtc.voiceservice.exception.UserException;
import webrtc.voiceservice.exception.UserException.NotExistUserException;

@RestControllerAdvice
public class GlobalUserExceptionHandler {

    @ExceptionHandler(NotExistUserException.class)
    protected ResponseEntity<?> handleNotExistUserException(NotExistUserException e) {
        final ExceptionHttpStatusResponse httpStatusResponse = new ExceptionHttpStatusResponse("가입하지 않은 사용자 입니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, HttpStatus.NOT_FOUND);
    }
}
