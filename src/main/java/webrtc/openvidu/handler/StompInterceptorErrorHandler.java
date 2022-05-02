package webrtc.openvidu.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import webrtc.openvidu.exception.JwtException.CustomExpiredJwtException;

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
        TestDto testDto = new TestDto("OK", ex.getMessage());

        return prepareErrorMessage(clientMessage, testDto, "OK");
    }

    private Message<byte[]> prepareErrorMessage(Message<byte[]> clientMessage, TestDto testDto, String errorCode) {
        String message = new String();
        try {
            message = objectMapper.writeValueAsString(testDto);
        } catch (JsonProcessingException e) {
            System.out.println(e);
        }


        System.out.println("message = " + message);
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(errorCode);
        return MessageBuilder.createMessage(message.getBytes(), accessor.getMessageHeaders());
    }

    @AllArgsConstructor
    private static class TestDto {
        private String status;
        private String error;
    }

}
