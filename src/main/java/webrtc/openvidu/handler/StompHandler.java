package webrtc.openvidu.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import webrtc.openvidu.service.channel.ChannelService;
import webrtc.openvidu.service.chat.ChatService;
import webrtc.openvidu.service.jwt.JwtUserDetailsService;
import webrtc.openvidu.utils.JwtTokenUtil;

import java.security.Principal;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ChannelService channelService;
    private final ChatService chatService;

    // websocket을 통해 들어온 요청이 처리 되기전 실행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        switch (accessor.getCommand()) {
            case CONNECT:
            case SEND:
                String jwtToken = accessor.getFirstNativeHeader("jwt");
                String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
                jwtTokenUtil.validateToken(jwtToken, userDetails);
                break;
        }
        return message;
    }
}