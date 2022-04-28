package webrtc.openvidu.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.dto.ChatDto.ChatServerMessage;
import webrtc.openvidu.enums.ClientMessageType;
import webrtc.openvidu.service.channel.ChannelService;

import java.util.ArrayList;
import java.util.List;

import static webrtc.openvidu.enums.SocketServerMessageType.*;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChannelService channelService;

    /**
     * Chatting Room에 message 발송
     */
    public void sendChatMessage(ClientMessageType type, String channelId, String senderName, String chatMessage) {
        Channel channel = channelService.findOneChannelById(channelId);
        Long currentParticipants = channel.getCurrentParticipants();
        ChatServerMessage serverMessage = new ChatServerMessage(channelId);
        switch (type) {
            case CHAT:
                List<User> currentUsers = new ArrayList<>();
                serverMessage.setChatType(CHAT, senderName, chatMessage, currentParticipants, currentUsers);
                break;
            case ENTER:
                List<User> enterUsers = new ArrayList<>();
                serverMessage.setEnterType(RENEWAL, "[알림] ", "님이 채팅방에 입장했습니다.", currentParticipants, enterUsers);
                break;
            case EXIT:
                List<User> exitUsers = new ArrayList<>();
                serverMessage.setExitType(RENEWAL, "[알림] ", "님이 채팅방에서 퇴장했습니다.", currentParticipants, exitUsers);
                break;
        }
        redisTemplate.convertAndSend(channelTopic.getTopic(), serverMessage);
    }
}
