package webrtc.openvidu.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import webrtc.openvidu.repository.ChannelRepository;
import webrtc.openvidu.service.channel.ChannelService;
import webrtc.openvidu.service.chat.ChatService;
import webrtc.openvidu.utils.JwtTokenUtil;


@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final ChatService chatService;
    private final ChannelService channelService;
    private final ChannelRepository channelRepository;
    private final JwtTokenUtil jwtTokenUtil;

    // websocket을 통해 들어온 요청이 처리 되기전 실행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("accessor = " + accessor);
        switch (accessor.getCommand()) {
            case CONNECT:
                String jwtToken = accessor.getFirstNativeHeader("jwt");
                String username = accessor.getFirstNativeHeader("username");
//                jwtTokenUtil.validateToken(jwtToken, username);
                break;
        }
        return message;
    }
}