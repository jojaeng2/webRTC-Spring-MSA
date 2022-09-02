package webrtc.chatservice.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.ChatDto.ChatServerMessage;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.repository.channel.ChannelDBRepository;
import webrtc.chatservice.repository.chat.ChatLogRepository;
import webrtc.chatservice.repository.users.UsersRepository;
import webrtc.chatservice.service.rabbit.RabbitPublish;

import java.util.List;
import static webrtc.chatservice.enums.SocketServerMessageType.*;

@RequiredArgsConstructor
@Service
public class ChattingServiceImpl implements ChattingService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;

    private final ChatLogRepository chatLogRepository;
    private final UsersRepository usersRepository;
    private final ChannelDBRepository channelDBRepository;
    private final RabbitPublish rabbitPublish;
    private final ChatLogService chatLogService;

    /**
     * Chatting Room에 message 발송
     */
    @Transactional
    public void sendChatMessage(ClientMessageType type, String channelId, String nickname, String chatMessage, String senderEmail) {
        Channel channel = channelDBRepository.findChannelById(channelId);
        Long currentParticipants = channel.getCurrentParticipants();
        ChatServerMessage serverMessage = new ChatServerMessage(channelId);
        List<Users> currentUsers = usersRepository.findUsersByChannelId(channelId);
        Long logId;
        switch (type) {
            case CHAT:
                serverMessage.setMessageType(CHAT, nickname, chatMessage, currentParticipants, currentUsers, senderEmail);
                logId = chatLogService.saveChatLog(type, chatMessage, nickname, channel, senderEmail);
                serverMessage.setChatLogId(logId);
                break;
            case ENTER:
                chatMessage = "[알림] " + nickname+ " 님이 채팅방에 입장했습니다.";
                serverMessage.setMessageType(RENEWAL, nickname, chatMessage, currentParticipants, currentUsers, senderEmail);
                logId = chatLogService.saveChatLog(type, chatMessage, nickname, channel, senderEmail);
                serverMessage.setChatLogId(logId);
                break;
            case EXIT:
                chatMessage = "[알림]" + nickname+ " 님이 채팅방에서 퇴장했습니다.";
                serverMessage.setMessageType(RENEWAL, nickname, chatMessage, currentParticipants, currentUsers, senderEmail);
                logId = chatLogService.saveChatLog(type, chatMessage, nickname, channel, senderEmail);
                serverMessage.setChatLogId(logId);
                break;
            case CLOSE:
                serverMessage.setMessageType(CLOSE, nickname, chatMessage, currentParticipants, currentUsers, senderEmail);
                logId = chatLogService.saveChatLog(type, chatMessage, nickname, channel, senderEmail);
                serverMessage.setChatLogId(logId);
                break;
            case REENTER:
                serverMessage.setMessageType(RENEWAL, nickname, chatMessage, currentParticipants, currentUsers, senderEmail);
                break;
        }
        rabbitPublish.publishMessage(serverMessage, type);
        redisTemplate.convertAndSend(channelTopic.getTopic(), serverMessage);
    }

}
