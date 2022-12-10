package webrtc.v1.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.entity.ChannelUser;
import webrtc.v1.channel.exception.ChannelException.NotExistChannelException;
import webrtc.v1.channel.repository.ChannelCrudRepository;
import webrtc.v1.channel.service.ChannelLifeService;
import webrtc.v1.chat.dto.ChattingMessage;
import webrtc.v1.chat.service.factory.ChattingMessageFactory;
import webrtc.v1.chat.enums.ClientMessageType;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.user.repository.ChannelUserRepository;
import webrtc.v1.user.repository.UsersRepository;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static webrtc.v1.chat.enums.ClientMessageType.REENTER;

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
    private final UsersRepository usersRepository;
    private final ChannelLifeService channelLifeService;

    /**
     * Chatting Room에 message 발송
     */
    @Transactional
    public void send(ClientMessageType type, String channelId, String chatMessage, String userId) {
        Users user = usersRepository.findById(UUID.fromString(userId))
                .orElseThrow(NotExistUserException::new);
        Channel channel = channelCrudRepository.findById(channelId)
                .orElseThrow(NotExistChannelException::new);
        List<Users> channelUsers = channelUserRepository.findByChannel(channel)
                .stream()
                .map(ChannelUser::getUser)
                .collect(toList());
        if (isReenter(type)) {
            ChattingMessage serverMessage = chattingMessageFactory.createMessage(channel, type, chatMessage, channelUsers, 0L, user);
            sendToRedis(serverMessage);
        }
        if (isNotReenter(type)) {
            long logIdx = chatLogService.save(type, chatMessage, channel, user);
            ChattingMessage serverMessage = chattingMessageFactory.createMessage(channel, type, chatMessage, channelUsers, logIdx, user);
            sendToRedis(serverMessage);
        }
    }

    @Transactional
    public void closeChannel(ClientMessageType type, String channelId) {
        Channel channel = channelCrudRepository.findById(channelId)
                .orElseThrow(NotExistChannelException::new);
        ChattingMessage chattingMessage = chattingMessageFactory.closeMessage(channel);
        channelLifeService.delete(channelId);
        sendToRedis(chattingMessage);
    }

    boolean isReenter(ClientMessageType type) {
        return type == REENTER;
    }

    boolean isNotReenter(ClientMessageType type) {
        return type != REENTER;
    }

    void sendToRedis(ChattingMessage message) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}
