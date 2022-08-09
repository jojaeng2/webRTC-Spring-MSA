package webrtc.chatservice.service.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.config.RabbitmqConfig;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChatLog;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.ChatDto.ChatServerMessage;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.repository.channel.ChannelDBRepository;
import webrtc.chatservice.repository.chat.ChatLogRepository;
import webrtc.chatservice.repository.users.UsersRepository;

import java.util.List;
import static webrtc.chatservice.enums.SocketServerMessageType.*;

@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService{

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;

    private final ChatLogRepository chatLogRepository;
    private final UsersRepository usersRepository;
    private final ChannelDBRepository channelDBRepository;

    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    private final String exchangeName = RabbitmqConfig.topicExchangeName;
    private final String chatTextRoutingKey = RabbitmqConfig.chatTextRoutingKey;
    private final String chatEnterRoutingKey = RabbitmqConfig.chatEnterRoutingKey;
    private final String chatExitRoutingKey = RabbitmqConfig.chatExitRoutingKey;


    @Transactional
    public Long saveChatLog(ClientMessageType type, String chatMessage, String nickname, Channel channel, String senderEmail) {
        List<ChatLog> findChatLogs = chatLogRepository.findLastChatLogsByChannelId(channel.getId());
        ChatLog chatLog = new ChatLog(type, chatMessage, nickname, senderEmail);

        if(findChatLogs.isEmpty()) chatLog.setChatLogIdx(1L);
        else chatLog.setChatLogIdx(findChatLogs.get(0).getIdx()+1);
        channel.addChatLog(chatLog);
        chatLogRepository.save(chatLog);
        return chatLog.getIdx();
    }

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
                logId = saveChatLog(type, chatMessage, nickname, channel, senderEmail);
                serverMessage.setChatLogId(logId);
                try {
                    String chatRabbitMessage = objectMapper.writeValueAsString(serverMessage);
                    rabbitTemplate.convertAndSend(exchangeName, chatTextRoutingKey, chatRabbitMessage);
                } catch (Exception e) {
                    System.out.println("e = " + e);

                    System.out.println("chatRabbitMessage Send Fail!!");
                }
                break;
            case ENTER:
                chatMessage = "[알림] " + nickname+ " 님이 채팅방에 입장했습니다.";
                serverMessage.setMessageType(RENEWAL, nickname, chatMessage, currentParticipants, currentUsers, senderEmail);
                logId = saveChatLog(type, chatMessage, nickname, channel, senderEmail);
                serverMessage.setChatLogId(logId);
                try {
                    String enterRabbitMessage = objectMapper.writeValueAsString(serverMessage);
                    rabbitTemplate.convertAndSend(exchangeName, chatEnterRoutingKey, enterRabbitMessage);
                } catch (Exception e) {
                    System.out.println("e = " + e);
                    System.out.println("enterRabbitMessage Send Fail!!");
                }
                break;
            case EXIT:
                chatMessage = "[알림]" + nickname+ " 님이 채팅방에서 퇴장했습니다.";
                serverMessage.setMessageType(RENEWAL, nickname, chatMessage, currentParticipants, currentUsers, senderEmail);
                logId = saveChatLog(type, chatMessage, nickname, channel, senderEmail);
                serverMessage.setChatLogId(logId);
                try {
                    String exitRabbitMessage = objectMapper.writeValueAsString(serverMessage);
                    rabbitTemplate.convertAndSend(exchangeName, chatExitRoutingKey, exitRabbitMessage);
                } catch (Exception e) {
                    System.out.println("exitRabbitMessage Send Fail!!");
                }
                break;
            case CLOSE:
                serverMessage.setMessageType(CLOSE, nickname, chatMessage, currentParticipants, currentUsers, senderEmail);
                logId = saveChatLog(type, chatMessage, nickname, channel, senderEmail);
                serverMessage.setChatLogId(logId);
                break;
            case REENTER:
                serverMessage.setMessageType(RENEWAL, nickname, chatMessage, currentParticipants, currentUsers, senderEmail);
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
