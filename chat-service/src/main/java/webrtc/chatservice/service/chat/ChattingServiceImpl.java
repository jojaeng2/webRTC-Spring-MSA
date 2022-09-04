package webrtc.chatservice.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.ChatDto;
import webrtc.chatservice.dto.ChatDto.ChattingMessage;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.repository.channel.ChannelDBRepository;
import webrtc.chatservice.repository.users.UsersRepository;
import webrtc.chatservice.service.rabbit.RabbitPublish;

import java.util.List;

import static webrtc.chatservice.enums.ClientMessageType.REENTER;
import static webrtc.chatservice.enums.SocketServerMessageType.*;

@RequiredArgsConstructor
@Service
public class ChattingServiceImpl implements ChattingService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;

    private final UsersRepository usersRepository;
    private final ChannelDBRepository channelDBRepository;
    private final RabbitPublish rabbitPublish;
    private final ChatLogService chatLogService;
    private final ChattingMessageFactory chattingMessageFactory;

    /**
     * Chatting Room에 message 발송
     */
    @Transactional
    public void sendChatMessage(ClientMessageType type, String channelId, String nickname, String chatMessage, String senderEmail) {
        Channel channel = channelDBRepository.findChannelById(channelId);
        Long currentParticipants = channel.getCurrentParticipants();
        List<Users> currentUsers = usersRepository.findUsersByChannelId(channelId);
        ChattingMessage serverMessage;

        if(type != REENTER) {
            Long logIdx = chatLogService.saveChatLog(type, chatMessage, nickname, channel, senderEmail);
            serverMessage = chattingMessageFactory.createMessage(channelId, type, nickname, chatMessage, currentParticipants, currentUsers, logIdx, senderEmail);
        }
        else {
            serverMessage = chattingMessageFactory.createMessage(channelId, type, nickname, chatMessage, currentParticipants, currentUsers, 0L, senderEmail);
        }

        rabbitPublish.publishMessage(serverMessage, type);
        redisTemplate.convertAndSend(channelTopic.getTopic(), serverMessage);
    }

}
