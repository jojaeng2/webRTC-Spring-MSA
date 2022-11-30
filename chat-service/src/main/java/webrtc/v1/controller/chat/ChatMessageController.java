package webrtc.v1.controller.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import webrtc.v1.domain.Users;
import webrtc.v1.dto.chat.ClientMessage;
import webrtc.v1.enums.ClientMessageType;
import webrtc.v1.service.chat.ChattingService;
import webrtc.v1.service.chat.factory.SocketMessageFactory;
import webrtc.v1.service.user.UsersService;
import webrtc.v1.utils.jwt.JwtTokenUtil;

import static webrtc.v1.enums.ClientMessageType.*;


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
        Users user = usersService.findOneByEmail(senderEmail);

        if(clientMessageType.equals(ENTER) || clientMessageType.equals(EXIT) || clientMessageType.equals(CHAT)) {
            socketMessageFactory.execute(clientMessageType, message, user, channelId);
        }

        chattingService.sendChatMessage(clientMessageType, channelId, message.getMessage(), user);
    }
}
