package webrtc.openvidu.controller.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import webrtc.openvidu.dto.ChatDto.*;
import webrtc.openvidu.enums.ClientMessageType;
import webrtc.openvidu.service.channel.ChannelService;
import webrtc.openvidu.service.chat.ChatService;
import webrtc.openvidu.utils.JwtTokenUtil;


@RequiredArgsConstructor
@Controller
public class ChatMessageController {

    private final ChatService chatService;
    private final ChannelService channelService;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * /pub/chat/room 으로 오는 메시지 반환
     */
    @MessageMapping("/chat/room")
    public void message(ClientMessage message, @Header("jwt") String jwtToken) {
        ClientMessageType clientMessageType = message.getType();
        String channelId = message.getChannelId();
        String senderName = jwtTokenUtil.getUsernameFromToken(jwtToken);
        String chatMessage = message.getMessage();
        switch(clientMessageType) {
            case CHAT:
                message.setSenderName(senderName);
                break;
            case ENTER:
                message.setSenderName("[알림] ");
                break;
            case EXIT:
                channelService.exitChannel(channelId, senderName);
                message.setSenderName("[알림] ");
                break;

        }
        chatService.sendChatMessage(clientMessageType, channelId, senderName, chatMessage);
    }
}
