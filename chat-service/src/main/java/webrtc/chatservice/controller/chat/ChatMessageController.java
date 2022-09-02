package webrtc.chatservice.controller.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.ChatDto.*;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.service.channel.ChannelService;
import webrtc.chatservice.service.chat.ChattingService;
import webrtc.chatservice.service.users.UsersService;
import webrtc.chatservice.utils.JwtTokenUtil;


@RequiredArgsConstructor
@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChatMessageController {

    private final ChattingService chattingService;
    private final ChannelService channelService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UsersService usersService;

    /**
     * /pub/chat/room 으로 오는 메시지 반환
     */
    @MessageMapping("/chat/room")
    public void message(ClientMessage message, @Header("jwt") String jwtToken, @Header("channelId") String channelId, @Header("type")ClientMessageType clientMessageType) {
        String senderEmail = jwtTokenUtil.getUserEmailFromToken(jwtToken);
        Users sender = usersService.findOneUserByEmail(senderEmail);
        String chatMessage = message.getMessage();
        switch(clientMessageType) {
            case CHAT:
                message.setSenderName(sender.getNickname());
                break;
            case EXIT:
                channelService.exitChannel(channelId, sender.getId());
                break;
        }
        chattingService.sendChatMessage(clientMessageType, channelId, sender.getNickname(), chatMessage, senderEmail);
    }
}
