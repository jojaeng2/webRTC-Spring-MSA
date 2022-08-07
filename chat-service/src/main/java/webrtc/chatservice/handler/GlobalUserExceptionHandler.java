package webrtc.chatservice.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import webrtc.chatservice.dto.HttpStatusResponse;
import webrtc.chatservice.exception.UserException.NotExistUserException;

@RestControllerAdvice
@ControllerAdvice
public class GlobalUserExceptionHandler {

    @ExceptionHandler(NotExistUserException.class)
    protected ResponseEntity<?> handleNotExistUserException(NotExistUserException e) {
        final HttpStatusResponse httpStatusResponse = new HttpStatusResponse("존재하지 않는 Users 입니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, HttpStatus.NOT_FOUND);
    }



}
