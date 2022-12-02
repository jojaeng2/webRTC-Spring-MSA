package webrtc.v1.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import webrtc.v1.dto.HttpStatusResponse;
import webrtc.v1.user.exception.UserException.NotExistUserException;

@RestControllerAdvice
@ControllerAdvice
public class GlobalUserExceptionHandler {

    @ExceptionHandler(NotExistUserException.class)
    protected ResponseEntity<?> handleNotExistUserException(NotExistUserException e) {
        final HttpStatusResponse httpStatusResponse = new HttpStatusResponse("존재하지 않는 Users 입니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, HttpStatus.NOT_FOUND);
    }



}
