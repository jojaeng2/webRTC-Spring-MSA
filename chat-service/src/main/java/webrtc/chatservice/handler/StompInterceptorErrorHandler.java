package webrtc.chatservice.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import webrtc.chatservice.exception.ChannelException;
import webrtc.chatservice.exception.ChannelException.AlreadyExistUserInChannelException;
import webrtc.chatservice.exception.ChannelException.ChannelExceptionDto;
import webrtc.chatservice.exception.ChannelException.ChannelParticipantsFullException;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.exception.JwtException.CustomJwtExceptionDto;
import webrtc.chatservice.exception.UserException.NotExistUserExceptionDto;
import webrtc.chatservice.service.chat.ChatService;

import static webrtc.chatservice.enums.SocketInterceptorErrorType.*;

@Component
@RequiredArgsConstructor
public class StompInterceptorErrorHandler extends StompSubProtocolErrorHandler {

    private final ObjectMapper objectMapper;
    private final ChatService chatService;

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
            channelExceptionDto.setField(ALREADY_FULL_CHANNEL, "????????? ????????? ????????? ????????? ???????????????.", 0L);
        }
        else if(NotExistChannelException.class.isInstance(exception)) {

            channelExceptionDto.setField(NOT_EXIST_CHANNEL, "????????? ???????????? ????????? ????????? ?????????????????????..", 0L);
        }
        else if(AlreadyExistUserInChannelException.class.isInstance(exception)) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(clientMessage);
            String connectChannelId = accessor.getFirstNativeHeader("channelId");
            Long idx = chatService.findLastChatLogsByChannelId(connectChannelId).getIdx();
            channelExceptionDto.setField(ALREADY_USER_IN_CHANNEL, "?????? ????????? ???????????? User?????????.", idx);
        }
        return prepareErrorMessage(clientMessage, channelExceptionDto, "Exception");
    }

    private Message<byte[]> handleAuthenticationException(Message<byte[]> clientMessage, Throwable exception) {
        NotExistUserExceptionDto notExistUserExceptionDto = new NotExistUserExceptionDto(INTERNAL_ERROR, "Internal Server Error 500");
        if(UsernameNotFoundException.class.isInstance(exception)) {
            notExistUserExceptionDto.setField(NOT_FOUND_USER_BY_JWT_ACCESS_TOKEN, "Jwt Access Token?????? User??? ?????? ?????? ????????????.");
        }
        return prepareErrorMessage(clientMessage, notExistUserExceptionDto, "Exception");
    }

    private Message<byte[]> handleJwtException(Message<byte[]> clientMessage, Throwable exception) {
        CustomJwtExceptionDto customJwtExceptionDto = new CustomJwtExceptionDto(INTERNAL_ERROR, "Internal Server Error 500");
        if(ExpiredJwtException.class.isInstance(exception)) {
            customJwtExceptionDto.setField(UNSUPPORTED_JWT_ACCESS_TOKEN, "Jwt Access Token??? ?????????????????????.");
        }
        else if(UnsupportedJwtException.class.isInstance(exception)) {
            customJwtExceptionDto.setField(UNSUPPORTED_JWT_ACCESS_TOKEN, "???????????? ?????? Jwt Access Token ???????????????.");
        }
        else if(MalformedJwtException.class.isInstance(exception)) {
            customJwtExceptionDto.setField(UNSUPPORTED_JWT_ACCESS_TOKEN, "????????? Jwt Access Token??? ?????????????????????.");
        }
        else if(SignatureException.class.isInstance(exception)) {
            customJwtExceptionDto.setField(UNSUPPORTED_JWT_ACCESS_TOKEN, "JWT Signature??? ???????????? ????????????.");
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
