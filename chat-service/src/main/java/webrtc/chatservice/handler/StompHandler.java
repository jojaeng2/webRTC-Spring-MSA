package webrtc.chatservice.handler;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import webrtc.chatservice.service.channel.ChannelFindService;
import webrtc.chatservice.service.channel.ChannelIOService;
import webrtc.chatservice.service.jwt.JwtUserDetailsService;
import webrtc.chatservice.utils.jwt.JwtTokenUtil;

import java.util.Objects;

import static org.springframework.messaging.simp.stomp.StompCommand.CONNECT;
import static org.springframework.messaging.simp.stomp.StompCommand.SEND;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ChannelIOService channelIOService;
    private final ChannelFindService channelFindService;

    // websocket을 통해 들어온 요청이 처리 되기전 실행
    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        if (isCommandConnect(command)) {
            checkValidateUsers(accessor);
        }
        if (isCommandSend(command)) {
            checkValidateUsers(accessor);
            String type = getType(accessor);
            String email = getEmail(accessor);
            String channelId = getChannelId(accessor);
            channelFindService.findOneChannelById(channelId);
            if (isEnter(type)) {
                channelIOService.enterChannel(channelId, email);
            }
        }
        return message;
    }

    void checkValidateUsers(StompHeaderAccessor accessor) {
        String jwtToken = getJwtToken(accessor);
        String email = getEmail(accessor);
        checkExistUser(jwtToken, email);
    }

    String getJwtToken(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("jwt");
    }

    String getEmail(StompHeaderAccessor accessor) {
        String jwtToken = getJwtToken(accessor);
        return jwtTokenUtil.getUserEmailFromToken(jwtToken);
    }

    void checkExistUser(String jwtToken, String email) {
        UserDetails connectUserDetails = jwtUserDetailsService.loadUserByUsername(email);
        jwtTokenUtil.validateToken(jwtToken, connectUserDetails);
    }

    String getType(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("type");
    }

    String getChannelId(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("channelId");
    }

    boolean isCommandConnect(StompCommand accessor) {
        return accessor == CONNECT;
    }

    boolean isCommandSend(StompCommand accessor) {
        return accessor == SEND;
    }

    boolean isEnter(String type) {
        return Objects.equals(type, "ENTER");
    }
}