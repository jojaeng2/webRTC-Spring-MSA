package webrtc.openvidu.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.dto.ChatDto.ClientMessage;
import webrtc.openvidu.dto.ChatDto.ServerMessage;
import webrtc.openvidu.repository.ChannelRepository;

import static webrtc.openvidu.enums.SocketServerMessageType.*;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChannelRepository channelRepository;

    /**
     * destination 정보에서 roomId 추출
     */
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf("/");
        if(lastIndex != -1) {
            return destination.substring(lastIndex+1);
        }
        return "";
    }

    /**
     * Chatting Room에 message 발송
     */
    public void sendChatMessage(ClientMessage message) {
        String channelId = message.getChannelId();
        String senderName = message.getSenderName();
        Channel channel = channelRepository.findOneChannelById(channelId);
        ServerMessage serverMessage = new ServerMessage(CHAT, channelId, message.getSenderName(), message.getMessage(), channel.getCurrentParticipants());
        switch (message.getType()) {
            case ENTER:
                serverMessage.setSenderName("[알림] ");
                serverMessage.setMessage(senderName + " 님이 입장했습니다.");
                break;
            case EXIT:
                serverMessage.setSenderName("[알림] ");
                serverMessage.setMessage(senderName + "님이 나갔습니다.");
                break;
        }
        redisTemplate.convertAndSend("/sub/chat/room" + channelId, serverMessage);
    }
}
