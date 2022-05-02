package webrtc.openvidu.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import webrtc.openvidu.dto.HttpStatusResponse;
import webrtc.openvidu.exception.ChannelException;
import webrtc.openvidu.exception.ChannelException.AlreadyExistChannelException;
import webrtc.openvidu.exception.ChannelException.AlreadyExistUserEnterChannelException;
import webrtc.openvidu.exception.ChannelException.ChannelParticipantsFullException;
import webrtc.openvidu.exception.ChannelException.NotExistChannelException;
import webrtc.openvidu.exception.UserException.NotExistUserException;

@RestControllerAdvice
public class GlobalSocketExceptionHandler {

    @ExceptionHandler(NotExistUserException.class)
    protected ResponseEntity<?> handleNotExistUserException(NotExistUserException e) {
        final HttpStatusResponse httpStatusResponse = new HttpStatusResponse("존재하지 않는 User 입니다.", e.getMessage());
        return new ResponseEntity<>(httpStatusResponse, HttpStatus.CONFLICT);
    }

//    @MessageExceptionHandler(NotExistUserException.class)
//    protected HttpStatusResponse handleNotExistUserException(NotExistUserException e) {
//        return new HttpStatusResponse("존재하지 않는 User 입니다.", e.getMessage());
//    }

    @MessageExceptionHandler(AlreadyExistChannelException.class)
    protected HttpStatusResponse handleAlreadyExistChannelException(AlreadyExistChannelException e) {
        return new HttpStatusResponse("이미 채널이 존재합니다.", e.getMessage());
    }

    @MessageExceptionHandler(NotExistChannelException.class)
    protected HttpStatusResponse handleNotExistChannel(NotExistChannelException e) {
        return new HttpStatusResponse("존재하지 않는 채널입니다.", e.getMessage());
    }

    @MessageExceptionHandler(AlreadyExistUserEnterChannelException.class)
    protected HttpStatusResponse handleAlreadyExistUserEnterChannelException(AlreadyExistUserEnterChannelException e) {
        return new HttpStatusResponse("이미 참여중인 채널입니다.", e.getMessage());
    }

    @MessageExceptionHandler(ChannelParticipantsFullException.class)
    protected HttpStatusResponse handleChannelParticipantsFullException(ChannelParticipantsFullException e) {
        return new HttpStatusResponse("채널에 인원이 가득찼습니다.", e.getMessage());
    }
}
