package webrtc.chatservice.controller.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import webrtc.chatservice.dto.chat.ClientMessage;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.service.channel.ChannelIOService;
import webrtc.chatservice.service.chat.ChattingService;
import webrtc.chatservice.service.users.UsersService;
import webrtc.chatservice.utils.JwtTokenUtil;


@RequiredArgsConstructor
@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChatMessageController {

    private final ChattingService chattingService;
    private final ChannelIOService channelIOService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UsersService usersService;

    /**
     * /pub/chat/room 으로 오는 메시지 반환
     */
    @MessageMapping("/chat/room")
    public void message(ClientMessage message, @Header("jwt") String jwtToken, @Header("channelId") String channelId, @Header("type")ClientMessageType clientMessageType) {
        String senderEmail = jwtTokenUtil.getUserEmailFromToken(jwtToken);
        String nickname = usersService.findOneUserByEmail(senderEmail).getNickname();
        String chatMessage = message.getMessage();
        switch(clientMessageType) {
            case CHAT:
                message.setSenderName(nickname);
                break;
            case EXIT:
                channelIOService.exitChannel(channelId, nickname);
                chatMessage = "[알림]" + nickname+ " 님이 채팅방에서 퇴장했습니다.";
                break;
            case ENTER:
                chatMessage = "[알림] " + nickname+ " 님이 채팅방에 입장했습니다.";
                break;

        }
        chattingService.sendChatMessage(clientMessageType, channelId, nickname, chatMessage, senderEmail);
    }
}
