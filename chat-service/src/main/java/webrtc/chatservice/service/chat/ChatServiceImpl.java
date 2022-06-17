package webrtc.chatservice.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChatLog;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.ChatDto.ChatServerMessage;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.repository.channel.ChannelRepository;
import webrtc.chatservice.repository.chat.ChatLogRepository;
import webrtc.chatservice.repository.user.UserRepository;

import java.util.List;
import static webrtc.chatservice.enums.SocketServerMessageType.*;

@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService{

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;

    private final ChatLogRepository chatLogRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

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
    @Transactional
    public void sendChatMessage(ClientMessageType type, String channelId, String senderName, String chatMessage, String senderEmail) {
        Channel channel = channelRepository.findChannelsById(channelId).get(0);
        Long currentParticipants = channel.getCurrentParticipants();
        ChatServerMessage serverMessage = new ChatServerMessage(channelId);
        List<User> currentUsers = userRepository.findUsersByChannelId(channelId);
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

    @Transactional
    public List<ChatLog> findChatLogsByIndex(String channelId, Long idx) {
        return chatLogRepository.findChatLogsByChannelId(channelId, idx);
    }

    @Transactional
    public ChatLog findLastChatLogsByChannelId(String channelId) {
        return chatLogRepository.findLastChatLogsByChannelId(channelId).get(0);
    }
}
