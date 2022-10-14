package webrtc.chatservice.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.exception.ChannelException;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.repository.channel.ChannelCrudRepository;
import webrtc.chatservice.repository.channel.ChannelListRepository;
import webrtc.chatservice.repository.users.ChannelUserRepository;
import webrtc.chatservice.repository.users.UsersRepository;
import webrtc.chatservice.service.chat.factory.ChattingMessageFactory;
import webrtc.chatservice.service.rabbit.RabbitPublish;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static webrtc.chatservice.enums.ClientMessageType.REENTER;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChattingServiceImpl implements ChattingService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;

    private final ChannelCrudRepository channelCrudRepository;
    private final ChatLogService chatLogService;
    private final ChattingMessageFactory chattingMessageFactory;
    private final ChannelUserRepository channelUserRepository;

    /**
     * Chatting Room에 message 발송
     */
    @Transactional
    public void sendChatMessage(ClientMessageType type, String channelId, String chatMessage, Users user) {
        Channel channel = channelCrudRepository.findById(channelId).orElseThrow(NotExistChannelException::new);
        List<Users> channelUsers = channelUserRepository.findByChannel(channel).stream()
                .map(ChannelUser::getUser)
                .collect(toList());
        ChattingMessage serverMessage;

        if(type != REENTER) {
            long logIdx = chatLogService.saveChatLog(type, chatMessage, channel, user);
            serverMessage = chattingMessageFactory.createMessage(channel, type, chatMessage, channelUsers, logIdx, user);
        } else {
            serverMessage = chattingMessageFactory.createMessage(channel, type, chatMessage, channelUsers, 0L, user);
        }
//        rabbitPublish.publishMessage(serverMessage, type);
        redisTemplate.convertAndSend(channelTopic.getTopic(), serverMessage);
    }

}
