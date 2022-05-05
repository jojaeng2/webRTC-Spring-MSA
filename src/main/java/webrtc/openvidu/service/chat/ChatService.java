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
import webrtc.openvidu.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static webrtc.openvidu.enums.SocketServerMessageType.*;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChannelService channelService;
    private final UserService userService;

    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if(lastIndex != -1) {
            return destination.substring(lastIndex+1);
        }
        return "";
    }

    /**
     * Chatting Room에 message 발송
     */
    public void sendChatMessage(ClientMessageType type, String channelId, String senderName, String chatMessage) {
        Channel channel = channelService.findOneChannelById(channelId);
        Long currentParticipants = channel.getCurrentParticipants();
        ChatServerMessage serverMessage = new ChatServerMessage(channelId);
        List<User> currentUsers = userService.findUsersByChannelId(channelId);
        switch (type) {
            case CHAT:
                serverMessage.setMessageType(CHAT, senderName, chatMessage, currentParticipants, currentUsers);
                break;
            case ENTER:
                serverMessage.setMessageType(RENEWAL, senderName, senderName+ " 님이 채팅방에 입장했습니다.", currentParticipants, currentUsers);
                break;
            case EXIT:
                serverMessage.setMessageType(RENEWAL, senderName, senderName+ " 님이 채팅방에서 퇴장했습니다.", currentParticipants, currentUsers);
                break;
        }
        redisTemplate.convertAndSend(channelTopic.getTopic(), serverMessage);
    }
}
