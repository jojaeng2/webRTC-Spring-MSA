package webrtc.openvidu.controller.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import webrtc.openvidu.domain.Channel;
<<<<<<< HEAD:backend/src/main/java/webrtc/openvidu/controller/channel/ChatMessageController.java
import webrtc.openvidu.dto.ChatDto;
import webrtc.openvidu.dto.ChatDto.ChatServerMessage;
import webrtc.openvidu.dto.ChatDto.ClientMessage;
import webrtc.openvidu.dto.ChatDto.ServerNoticeMessage;
=======
import webrtc.openvidu.dto.ChatDto.*;
import webrtc.openvidu.enums.ChannelServiceReturnType;
>>>>>>> 18cdb6bb39d5ef24329da9b5e0f6d53d239ef993:src/main/java/webrtc/openvidu/controller/channel/ChatMessageController.java
import webrtc.openvidu.enums.ClientMessageType;
import webrtc.openvidu.repository.ChannelRepository;
import webrtc.openvidu.service.channel.ChannelService;
import webrtc.openvidu.service.chat.ChatService;

import static webrtc.openvidu.enums.SocketServerMessageType.RENEWAL;


@RequiredArgsConstructor
@Controller
public class ChatMessageController {

    private final ChannelService channelService;
    private final ChatService chatService;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;
    private final ChannelRepository channelRepository;

    /**
     * /pub/chat/room 으로 오는 메시지 반환
     */
    @MessageMapping("/chat/room")
    public void message(ClientMessage message, @Header("jwt") String jwtToken) throws Exception {
        System.out.println(jwtToken);
        System.out.println("ChatMessageController message Method");
        ClientMessageType clientMessageType = message.getType();
        String channelId = message.getChannelId();
        String senderName = message.getSenderName();
<<<<<<< HEAD:backend/src/main/java/webrtc/openvidu/controller/channel/ChatMessageController.java
        System.out.println("senderName = " + senderName);
=======
        String chatMessage = message.getMessage();
        System.out.println("senderName = " + senderName);
        System.out.println("chatMessage = " + chatMessage);
        System.out.println("channelId = " + channelId);
>>>>>>> 18cdb6bb39d5ef24329da9b5e0f6d53d239ef993:src/main/java/webrtc/openvidu/controller/channel/ChatMessageController.java
        switch(clientMessageType) {
            case CHAT:
                message.setSenderName(senderName);
                break;
            case ENTER:
                channelService.enterChannel(channelId, senderName);
<<<<<<< HEAD:backend/src/main/java/webrtc/openvidu/controller/channel/ChatMessageController.java
                ServerNoticeMessage enterNoticeMessage = new ServerNoticeMessage(RENEWAL, channelId, "[알림] ", senderName + "님이 입장했습니다.", enterChannel.getCurrentParticipants());
                redisPublisher.publish(channelRepository.getTopic(channelId), enterNoticeMessage);
                break;
            case EXIT:
                Channel exitChannel = channelService.leaveChannel(channelId, senderName);
                ServerNoticeMessage exitNoticeMessage = new ServerNoticeMessage(RENEWAL, channelId, "[알림] ", senderName + "님이 퇴장했습니다.", exitChannel.getCurrentParticipants());
                redisPublisher.publish(channelRepository.getTopic(channelId), exitNoticeMessage);
=======
                break;
            case EXIT:
                channelService.exitChannel(channelId, senderName);
>>>>>>> 18cdb6bb39d5ef24329da9b5e0f6d53d239ef993:src/main/java/webrtc/openvidu/controller/channel/ChatMessageController.java
                break;
        }
        chatService.sendChatMessage(clientMessageType, channelId, senderName, chatMessage);
    }
}
