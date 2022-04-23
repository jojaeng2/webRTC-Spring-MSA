package webrtc.openvidu.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import webrtc.openvidu.service.jwt.JwtTokenProvider;

@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("accessor = " + accessor);
        if(accessor.getCommand() == StompCommand.CONNECT) {
            if(!jwtTokenProvider.validateToken(accessor.getFirstNativeHeader("token")))
                throw new AccessDeniedException("");
        }
        return message;
    }
}
