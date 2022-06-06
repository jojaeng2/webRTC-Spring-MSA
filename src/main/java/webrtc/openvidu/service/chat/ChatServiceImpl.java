package webrtc.openvidu.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.domain.ChatLog;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.dto.ChatDto.ChatServerMessage;
import webrtc.openvidu.enums.ClientMessageType;
import webrtc.openvidu.enums.SocketServerMessageType;
import webrtc.openvidu.repository.chat.ChatLogRepository;
import webrtc.openvidu.service.channel.ChannelService;
import webrtc.openvidu.service.user.UserService;

import java.util.List;
import static webrtc.openvidu.enums.SocketServerMessageType.*;

@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService{

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;

    private final ChatLogRepository chatLogRepository;

    private final UserService userService;
    private final ChannelService channelService;


    public Long saveChatLog(ClientMessageType type, String chatMessage, String username, Channel channel, String senderEmail) {
        List<ChatLog> findChatLogs = chatLogRepository.findLastChatLogsByChannelId(channel.getId());
        ChatLog chatLog = new ChatLog(type, chatMessage, username, senderEmail);

        if(findChatLogs.isEmpty()) chatLog.setChatLogIdx(1L);
        else chatLog.setChatLogIdx(findChatLogs.get(0).getIdx()+1);

        chatLog.setChannel(channel);
        chatLogRepository.save(chatLog);
        return chatLog.getIdx();
    }

    /**
     * Chatting Room에 message 발송
     */
    public void sendChatMessage(ClientMessageType type, String channelId, String senderName, String chatMessage, String senderEmail) {
        Channel channel = channelService.findOneChannelById(channelId);
        Long currentParticipants = channel.getCurrentParticipants();
        ChatServerMessage serverMessage = new ChatServerMessage(channelId);
        List<User> currentUsers = userService.findUsersByChannelId(channelId);
        Long logId;
        switch (type) {
            case CHAT:
                serverMessage.setMessageType(CHAT, senderName, chatMessage, currentParticipants, currentUsers, senderEmail);
                logId = saveChatLog(type, chatMessage, senderName, channel, senderEmail);
                serverMessage.setChatLogId(logId);
                break;
            case ENTER:
                chatMessage = "[알림] " + senderName+ " 님이 채팅방에 입장했습니다.";
                serverMessage.setMessageType(RENEWAL, senderName, chatMessage, currentParticipants, currentUsers, senderEmail);
                logId = saveChatLog(type, chatMessage, senderName, channel, senderEmail);
                serverMessage.setChatLogId(logId);
                break;
            case EXIT:
                chatMessage = "[알림]" + senderName+ " 님이 채팅방에서 퇴장했습니다.";
                serverMessage.setMessageType(RENEWAL, senderName, chatMessage, currentParticipants, currentUsers, senderEmail);
                logId = saveChatLog(type, chatMessage, senderName, channel, senderEmail);
                serverMessage.setChatLogId(logId);
                break;
            case CLOSE:
                serverMessage.setMessageType(CLOSE, senderName, chatMessage, currentParticipants, currentUsers, senderEmail);
                logId = saveChatLog(type, chatMessage, senderName, channel, senderEmail);
                serverMessage.setChatLogId(logId);
                break;
            case REENTER:
                serverMessage.setMessageType(RENEWAL, senderName, chatMessage, currentParticipants, currentUsers, senderEmail);
                break;
        }
        redisTemplate.convertAndSend(channelTopic.getTopic(), serverMessage);
    }

    public List<ChatLog> findChatLogsByIndex(String channelId, Long idx) {
        return chatLogRepository.findChatLogsByChannelId(channelId, idx);
    }

    public ChatLog findLastChatLogsByChannelId(String channelId) {
        return chatLogRepository.findLastChatLogsByChannelId(channelId).get(0);
    }
}
