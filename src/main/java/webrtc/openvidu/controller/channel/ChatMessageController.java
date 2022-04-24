package webrtc.openvidu.controller.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.dto.ChatDto.*;
import webrtc.openvidu.enums.ClientMessageType;
import webrtc.openvidu.repository.ChannelRepository;
import webrtc.openvidu.service.channel.ChannelService;
import webrtc.openvidu.service.pubsub.RedisPublisher;

import static webrtc.openvidu.enums.SocketServerMessageType.CHAT;
import static webrtc.openvidu.enums.SocketServerMessageType.RENEWAL;


@RequiredArgsConstructor
@Controller
public class ChatMessageController {

    private final ChannelService channelService;
    private final RedisPublisher redisPublisher;
    private final ChannelRepository channelRepository;

    /**
     * /pub/chat/room 으로 오는 메시지 반환
     */
    @MessageMapping("/chat/room")
    public void message(ClientMessage message) {
        System.out.println("ChatMessageController message Method");
        ClientMessageType clientMessageType = message.getType();
        String channelId = message.getChannelId();
        String chatMessage = message.getMessage();
        String senderName = message.getSenderName();
        System.out.println("senderName = " + senderName);
        System.out.println("chatMessage = " + chatMessage);
        System.out.println("channelId = " + channelId);
        switch(clientMessageType) {
            case CHAT:
                Channel chatChannel = channelService.findOneChannelById(channelId);
                ChatServerMessage chatServerMessage = new ChatServerMessage(CHAT, channelId, senderName, chatMessage);
                redisPublisher.publish(channelRepository.getTopic(channelId), chatServerMessage);
                break;
            case ENTER:
                Channel enterChannel = channelService.findOneChannelById(channelId);
                channelService.enterChannel(channelId, senderName);
                ServerNoticeMessage enterNoticeMessage = new ServerNoticeMessage(RENEWAL, channelId, senderName + "님이 입장했습니다.", enterChannel.getCurrentParticipants());
                redisPublisher.publish(channelRepository.getTopic(channelId), enterNoticeMessage);
                break;
            case EXIT:
                Channel exitChannel = channelService.exitChannel(channelId, senderName);
                ServerNoticeMessage exitNoticeMessage = new ServerNoticeMessage(RENEWAL, channelId, senderName + "님이 퇴장했습니다.", exitChannel.getCurrentParticipants());
                redisPublisher.publish(channelRepository.getTopic(channelId), exitNoticeMessage);
                break;
        }
    }
}
