package webrtc.chatservice.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import webrtc.chatservice.dto.HttpStatusResponse;
import webrtc.chatservice.exception.ChannelException.AlreadyExistChannelException;
import webrtc.chatservice.exception.ChannelException.AlreadyExistUserInChannelException;
import webrtc.chatservice.exception.ChannelException.ChannelParticipantsFullException;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;

@RestControllerAdvice
public class GlobalChannelExceptionHandler {

    @ExceptionHandler(AlreadyExistChannelException.class)
    protected ResponseEntity<?> handleAlreadyExistChannelException(AlreadyExistChannelException e) {
        final HttpStatusResponse httpStatusResponse = new HttpStatusResponse("이미 채널이 존재합니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotExistChannelException.class)
    protected ResponseEntity<?> handleNotExistChannel(NotExistChannelException e) {
        final HttpStatusResponse httpStatusResponse = new HttpStatusResponse("존재하지 않는 채널입니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyExistUserInChannelException.class)
    protected ResponseEntity<?> handleAlreadyExistUserEnterChannelException(AlreadyExistUserInChannelException e) {
        final HttpStatusResponse httpStatusResponse = new HttpStatusResponse("이미 참여중인 채널입니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, HttpStatus.ALREADY_REPORTED);
    }

    @ExceptionHandler(ChannelParticipantsFullException.class)
    protected ResponseEntity<?> handleChannelParticipantsFullException(ChannelParticipantsFullException e) {
        final HttpStatusResponse httpStatusResponse = new HttpStatusResponse("채널에 인원이 가득찼습니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, HttpStatus.NOT_ACCEPTABLE);
    }
}
