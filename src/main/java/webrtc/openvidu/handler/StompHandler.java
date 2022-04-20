package webrtc.openvidu.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import webrtc.openvidu.dto.ChatDto;
import webrtc.openvidu.dto.ChatDto.ClientMessage;
import webrtc.openvidu.repository.ChannelRepository;
import webrtc.openvidu.service.channel.ChannelService;
import webrtc.openvidu.service.chat.ChatService;

import java.security.Principal;
import java.util.Optional;

import static webrtc.openvidu.enums.ClientMessageType.ENTER;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final ChatService chatService;
    private final ChannelService channelService;
    private final ChannelRepository channelRepository;

    // websocket을 통해 들어온 요청이 처리 되기전 실행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        switch (accessor.getCommand()) {
            case CONNECT:
                String jwtToken = accessor.getFirstNativeHeader("jwt");
//                jwtTokenProvider.validateToken(jwtToken);
                break;
        }
        return message;
    }
}