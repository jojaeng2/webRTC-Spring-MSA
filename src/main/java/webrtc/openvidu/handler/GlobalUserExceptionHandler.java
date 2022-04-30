package webrtc.openvidu.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import webrtc.openvidu.dto.HttpStatusResponse;
import webrtc.openvidu.exception.UserException.NotExistUserException;

@RestControllerAdvice
public class GlobalUserExceptionHandler {

    @ExceptionHandler(NotExistUserException.class)
    protected ResponseEntity<?> handleNotExistUserException(NotExistUserException e) {
        final HttpStatusResponse httpStatusResponse = new HttpStatusResponse("존재하지 않는 User 입니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, HttpStatus.CONFLICT);
    }

}
