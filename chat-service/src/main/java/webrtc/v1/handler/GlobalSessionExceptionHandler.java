package webrtc.v1.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import webrtc.v1.exception.VoiceException.*;

@RestControllerAdvice
public class GlobalSessionExceptionHandler {

    @ExceptionHandler(OpenViduClientException.class)
    protected ResponseEntity<?> handleOpenViduClientException(OpenViduClientException e) {
        final ExceptionHttpStatusResponse httpStatusResponse = new ExceptionHttpStatusResponse("내부 OpenViduServer 오류가 발생했습니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotExistOpenViduServerException.class)
    protected ResponseEntity<?> handleNotExistOpenViduServerException(NotExistOpenViduServerException e) {
        final ExceptionHttpStatusResponse httpStatusResponse = new ExceptionHttpStatusResponse("OpenViduServer를 찾을 수 없습니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyRemovedSessionInOpenViduServer.class)
    protected ResponseEntity<?> handleAlreadyRemovedSessionInOpenViduServer(AlreadyRemovedSessionInOpenViduServer e) {
        final ExceptionHttpStatusResponse httpStatusResponse = new ExceptionHttpStatusResponse("Openvidu Server에서 Session이 이미 삭제된 상태입니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidAccessToOpenViduServerException.class)
    protected ResponseEntity<?> handleInvalidAccessToOpenViduServerException(InvalidAccessToOpenViduServerException e) {
        final ExceptionHttpStatusResponse httpStatusResponse = new ExceptionHttpStatusResponse("올바르지 않은 Session token 형식입니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, HttpStatus.BAD_REQUEST);
    }
}
