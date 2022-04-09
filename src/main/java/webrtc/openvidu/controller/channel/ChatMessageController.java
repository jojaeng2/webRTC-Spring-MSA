package webrtc.openvidu.controller.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.dto.ChatDto.ChatServerMessage;
import webrtc.openvidu.dto.ChatDto.ClientMessage;
import webrtc.openvidu.dto.ChatDto.ServerMessage;
import webrtc.openvidu.enums.ClientMessageType;
import webrtc.openvidu.repository.ChannelRepository;
import webrtc.openvidu.service.channel.ChannelService;
import webrtc.openvidu.service.pubsub.RedisPublisher;

import java.security.Principal;
import java.util.Optional;

import static webrtc.openvidu.enums.ClientMessageType.ENTER;
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
        ClientMessageType clientMessageType = message.getType();
        String channelId = message.getChannelId();
        String clientChatMessage = message.getMessage();
        String senderName = message.getSenderName();
        switch(clientMessageType) {
            case CHAT:
                Channel chatChannel = channelService.findOneChannelById(channelId);
                ChatServerMessage chatServerMessage = new ChatServerMessage(CHAT, "userName", clientChatMessage);
                redisPublisher.publish(channelRepository.getTopic(channelId), chatServerMessage);
                break;
            case ENTER:
                Channel enterChannel = channelService.findOneChannelById(channelId);
                channelService.enterChannel(channelId, senderName);
                ServerMessage enterServerMessage = new ServerMessage(RENEWAL, senderName + "님이 입장했습니다.", channelId, enterChannel.getCurrentParticipants());
                redisPublisher.publish(channelRepository.getTopic(channelId), enterServerMessage);
                break;
            case EXIT:
                Channel leaveChannel = channelService.leaveChannel(channelId, senderName);
                ServerMessage leaveServerMessage = new ServerMessage(RENEWAL, senderName + "님이 퇴장했습니다.", channelId, leaveChannel.getCurrentParticipants());
                redisPublisher.publish(channelRepository.getTopic(channelId), leaveServerMessage);
                break;
        }
    }
}
