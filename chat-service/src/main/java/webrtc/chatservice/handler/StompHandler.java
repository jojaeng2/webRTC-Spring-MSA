package webrtc.chatservice.handler;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.service.channel.ChannelService;
import webrtc.chatservice.service.jwt.JwtUserDetailsService;
import webrtc.chatservice.utils.JwtTokenUtil;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ChannelService channelService;

    // websocket을 통해 들어온 요청이 처리 되기전 실행
    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("accessor = " + accessor.getCommand());
        System.out.println("accessor.getFirstNativeHeader(\"jwt\") = " + accessor.getFirstNativeHeader("jwt"));
        System.out.println("accessor.getFirstNativeHeader(\"type\") = " + accessor.getFirstNativeHeader("type"));
        System.out.println("accessor.getFirstNativeHeader(\"channelId\") = " + accessor.getFirstNativeHeader("channelId"));


        switch (accessor.getCommand()) {
            case CONNECT:
                String connectJwtToken = accessor.getFirstNativeHeader("jwt");
                String connectUserEmail = jwtTokenUtil.getUserEmailFromToken(connectJwtToken);
                UserDetails connectUserDetails = jwtUserDetailsService.loadUserByUsername(connectUserEmail);
                jwtTokenUtil.validateToken(connectJwtToken, connectUserDetails);
                break;
            case SEND:
                String sendJwtToken = accessor.getFirstNativeHeader("jwt");
                String messageType = accessor.getFirstNativeHeader("type");
                String sendUserEmail = jwtTokenUtil.getUserEmailFromToken(sendJwtToken);

                UserDetails sendUserDetails = jwtUserDetailsService.loadUserByUsername(sendUserEmail);
                jwtTokenUtil.validateToken(sendJwtToken, sendUserDetails);
                String sendChannelId = accessor.getFirstNativeHeader("channelId");

                // interceptor에서 예외를 터뜨리기 위해 존재.
                channelService.findOneChannelById(sendChannelId);

                if(messageType != null && messageType.equals("ENTER")) {
                    channelService.enterChannel(sendChannelId, sendUserEmail);
                }
                break;
        }
        return message;
    }

}