package webrtc.openvidu.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import webrtc.openvidu.exception.ChannelException;
import webrtc.openvidu.exception.ChannelException.ChannelExceptionDto;
import webrtc.openvidu.exception.ChannelException.ChannelParticipantsFullException;
import webrtc.openvidu.exception.ChannelException.NotExistChannelException;
import webrtc.openvidu.exception.JwtException.CustomJwtExceptionDto;
import webrtc.openvidu.exception.UserException.NotExistUserExceptionDto;

import static webrtc.openvidu.enums.SocketInterceptorErrorType.*;

@Component
@RequiredArgsConstructor
public class StompInterceptorErrorHandler extends StompSubProtocolErrorHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        Throwable exception = ex;
        System.out.println("handleClientMessageProcessingError = ");
        System.out.println("exception.getCause() = " + exception.getCause());
        if (exception instanceof MessageDeliveryException) {
            exception = exception.getCause();
            if(exception instanceof JwtException) {
                return handleJwtException(clientMessage, exception);
            }
            if(exception instanceof AuthenticationException) {
                return handleAuthenticationException(clientMessage, exception);
            }
            if(exception instanceof ChannelException) {
                return handleChannelException(clientMessage, exception);
            }
        }
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private Message<byte[]> handleChannelException(Message<byte[]> clientMessage, Throwable exception) {
        ChannelExceptionDto channelExceptionDto = new ChannelExceptionDto(INTERNAL_ERROR, "Internal Server Error 500");
        if(ChannelParticipantsFullException.class.isInstance(exception)) {
            channelExceptionDto.setField(ALREADY_FULL_CHANNEL, "채널에 인원이 가득차 입장할 수없습니다.");
        }
        else if(NotExistChannelException.class.isInstance(exception)) {
            channelExceptionDto.setField(NOT_EXIST_CHANNEL, "채널이 존재하지 않거나 시간이 만료되었습니다..");
        }
        return prepareErrorMessage(clientMessage, channelExceptionDto, "Exception");
    }

    private Message<byte[]> handleAuthenticationException(Message<byte[]> clientMessage, Throwable exception) {
        NotExistUserExceptionDto notExistUserExceptionDto = new NotExistUserExceptionDto(INTERNAL_ERROR, "Internal Server Error 500");
        if(UsernameNotFoundException.class.isInstance(exception)) {
            notExistUserExceptionDto.setField(NOT_FOUND_USER_BY_JWT_ACCESS_TOKEN, "Jwt Access Token으로 User를 찾을 수가 없습니다.");
        }
        return prepareErrorMessage(clientMessage, notExistUserExceptionDto, "Exception");
    }

    private Message<byte[]> handleJwtException(Message<byte[]> clientMessage, Throwable exception) {
        CustomJwtExceptionDto customJwtExceptionDto = new CustomJwtExceptionDto(INTERNAL_ERROR, "Internal Server Error 500");
        if(ExpiredJwtException.class.isInstance(exception)) {
            customJwtExceptionDto.setField(UNSUPPORTED_JWT_ACCESS_TOKEN, "Jwt Access Token이 만료되었습니다.");
        }
        else if(UnsupportedJwtException.class.isInstance(exception)) {
            customJwtExceptionDto.setField(UNSUPPORTED_JWT_ACCESS_TOKEN, "올바르지 않은 Jwt Access Token 형식입니다.");
        }
        else if(MalformedJwtException.class.isInstance(exception)) {
            customJwtExceptionDto.setField(UNSUPPORTED_JWT_ACCESS_TOKEN, "손상된 Jwt Access Token이 사용되었습니다.");
        }
        else if(SignatureException.class.isInstance(exception)) {
            customJwtExceptionDto.setField(UNSUPPORTED_JWT_ACCESS_TOKEN, "JWT Signature이 올바르지 않습니다.");
        }
        return prepareErrorMessage(clientMessage, customJwtExceptionDto, "Exception");
    }


    private Message<byte[]> prepareErrorMessage(Message<byte[]> clientMessage, Object requestObject, String errorCode) {
        String message = new String();
        try {
            message = objectMapper.writeValueAsString(requestObject);
        } catch (JsonProcessingException e) {
            System.out.println("prepareErrorMessage = " + e);
        }
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(errorCode);
        return MessageBuilder.createMessage(message.getBytes(), accessor.getMessageHeaders());
    }

}
