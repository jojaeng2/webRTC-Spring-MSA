package webrtc.openvidu.handler;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.exception.ChannelException;
import webrtc.openvidu.exception.ChannelException.AlreadyExistUserInChannelException;
import webrtc.openvidu.service.channel.ChannelService;
import webrtc.openvidu.service.jwt.JwtUserDetailsService;
import webrtc.openvidu.utils.JwtTokenUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

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

        switch (accessor.getCommand()) {
            case CONNECT:
                String connectJwtToken = accessor.getFirstNativeHeader("jwt");
                String connectUsername = jwtTokenUtil.getUsernameFromToken(connectJwtToken);
                UserDetails connectUserDetails = jwtUserDetailsService.loadUserByUsername(connectUsername);
                jwtTokenUtil.validateToken(connectJwtToken, connectUserDetails);
                break;
            case SEND:
                String sendJwtToken = accessor.getFirstNativeHeader("jwt");
                String messageType = accessor.getFirstNativeHeader("type");
                String sendUsername = jwtTokenUtil.getUsernameFromToken(sendJwtToken);
                if(messageType.equals("ENTER")) {
                    String connectChannelId = accessor.getFirstNativeHeader("channelId");
                    Channel connectCheckedExistChannel = channelService.findOneChannelById(connectChannelId);
                    channelService.enterChannel(connectCheckedExistChannel, sendUsername);
                }
                UserDetails sendUserDetails = jwtUserDetailsService.loadUserByUsername(sendUsername);
                jwtTokenUtil.validateToken(sendJwtToken, sendUserDetails);
                String sendChannelId = accessor.getFirstNativeHeader("channelId");
                channelService.findOneChannelById(sendChannelId);
                break;
        }
        return message;
    }

}