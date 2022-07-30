package webrtc.chatservice.controller.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.ChatDto.*;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.service.channel.ChannelService;
import webrtc.chatservice.service.chat.ChatService;
import webrtc.chatservice.service.user.UserService;
import webrtc.chatservice.utils.JwtTokenUtil;


@RequiredArgsConstructor
@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChatMessageController {

    private final ChatService chatService;
    private final ChannelService channelService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    /**
     * /pub/chat/room 으로 오는 메시지 반환
     */
    @MessageMapping("/chat/room")
    public void message(ClientMessage message, @Header("jwt") String jwtToken) {
        ClientMessageType clientMessageType = message.getType();
        String channelId = message.getChannelId();
        String senderEmail = jwtTokenUtil.getUserEmailFromToken(jwtToken);
        User sender = userService.findOneUserByEmail(senderEmail);
        String chatMessage = message.getMessage();
        switch(clientMessageType) {
            case CHAT:
                message.setSenderName(sender.getNickname());
                break;
            case EXIT:
                channelService.exitChannel(channelId, sender.getId());
                break;
        }
        chatService.sendChatMessage(clientMessageType, channelId, sender.getNickname(), chatMessage, senderEmail);
    }
}
