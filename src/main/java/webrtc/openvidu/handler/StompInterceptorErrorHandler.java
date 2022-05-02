package webrtc.openvidu.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import webrtc.openvidu.dto.JwtDto.CustomExpiredJwtExceptionDto;
import webrtc.openvidu.exception.JwtException.CustomExpiredJwtException;

import static webrtc.openvidu.enums.SocketInterceptorErrorType.JWT_EXPIRED;

@Component

public class StompInterceptorErrorHandler extends StompSubProtocolErrorHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        Throwable exception = ex;
        if (exception instanceof MessageDeliveryException) {
            exception = exception.getCause();
            if(exception instanceof CustomExpiredJwtException) {
                return handleCustomExpiredJwtException(clientMessage, exception);
            }
        }
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }



    private Message<byte[]> handleCustomExpiredJwtException(Message<byte[]> clientMessage, Throwable ex) {
        CustomExpiredJwtExceptionDto customExpiredJwtExceptionDto = new CustomExpiredJwtExceptionDto(JWT_EXPIRED, "Jwt Token이 만료되었습니다.");
        return prepareErrorMessage(clientMessage, customExpiredJwtExceptionDto, "OK");
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
