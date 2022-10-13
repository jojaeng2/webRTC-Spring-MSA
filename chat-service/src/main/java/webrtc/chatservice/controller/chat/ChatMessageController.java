package webrtc.chatservice.controller.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.chat.ClientMessage;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.service.chat.ChattingService;
import webrtc.chatservice.service.chat.factory.SocketMessageFactory;
import webrtc.chatservice.service.users.UsersService;
import webrtc.chatservice.utils.jwt.JwtTokenUtil;

import static webrtc.chatservice.enums.ClientMessageType.*;


@Slf4j
@RequiredArgsConstructor
@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChatMessageController {

    private final ChattingService chattingService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UsersService usersService;
    private final SocketMessageFactory socketMessageFactory;

    /**
     * /pub/chat/room 으로 오는 메시지 반환
     */
    @MessageMapping("/chat/room")
    public void message(ClientMessage message, @Header("jwt") String jwtToken, @Header("channelId") String channelId, @Header("type")ClientMessageType clientMessageType) {
        String senderEmail = jwtTokenUtil.getUserEmailFromToken(jwtToken);
        Users user = usersService.findOneUserByEmail(senderEmail);

        if(clientMessageType.equals(ENTER) || clientMessageType.equals(EXIT) || clientMessageType.equals(CHAT)) {
            socketMessageFactory.execute(clientMessageType, message, user, channelId);
        }

        chattingService.sendChatMessage(clientMessageType, channelId, user.getNickname(), message.getMessage(), senderEmail);
    }
}
