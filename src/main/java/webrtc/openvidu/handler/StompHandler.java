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
import webrtc.openvidu.service.chat.ChatService;
import webrtc.openvidu.service.jwt.JwtTokenProvider;

import java.security.Principal;
import java.util.Optional;

import static webrtc.openvidu.enums.ClientMessageType.ENTER;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatService chatService;

    // websocket을 통해 들어온 요청이 처리 되기전 실행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        switch (accessor.getCommand()) {
            case CONNECT:
                String jwtToken = accessor.getFirstNativeHeader("jwt");
                jwtTokenProvider.validateToken(jwtToken);
                break;
            case SUBSCRIBE:
                //Header에서 subscribe destination info를 얻고, roomId를 추출
                String roomId = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));

                // Client 입장 메시지를 채팅방에 발송한다. (redis publish)
                String senderName = Optional.ofNullable((Principal) message.getHeaders().get("simpUser")).map(Principal::getName).orElse("UnknownUser");
                ClientMessage clientMessage = new ClientMessage(ENTER, roomId, senderName);
                chatService.sendChatMessage(clientMessage);
                break;
            case DISCONNECT:

                break;
        }
        return message;
    }
}