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
import webrtc.v1.chat.dto.ChattingMessage;
import webrtc.v1.chat.enums.ClientMessageType;
import webrtc.v1.chat.service.factory.ChattingMessageFactory;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.channel.repository.ChannelUserRepository;
import webrtc.v1.user.repository.UsersRepository;

import java.util.List;

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

    /**
     * Chatting Room에 message 발송
     */
    @Transactional
    public void send(ClientMessageType type, String channelId, String chatMessage, String userId) {
        Users user = findUsersById(userId);
        Channel channel = findChannelById(channelId);
        List<Users> users = findUsersByChannel(channel);
        if (isReenter(type)) {
            ChattingMessage serverMessage = chattingMessageFactory.createMessage(channel, type, chatMessage, users, 0L, user);
            sendToRedis(serverMessage);
        }
        if (isNotReenter(type)) {
            long logIdx = chatLogService.save(type, chatMessage, channel, user);
            ChattingMessage serverMessage = chattingMessageFactory.createMessage(channel, type, chatMessage, users, logIdx, user);
            sendToRedis(serverMessage);
        }
    }

    public void closeChannel(Channel channel) {
        ChattingMessage chattingMessage = chattingMessageFactory.closeMessage(channel);
        sendToRedis(chattingMessage);
    }

    private boolean isReenter(ClientMessageType type) {
        return type == REENTER;
    }

    private boolean isNotReenter(ClientMessageType type) {
        return type != REENTER;
    }

    private void sendToRedis(ChattingMessage message) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }

    private Channel findChannelById(String id) {
        return channelCrudRepository.findById(id)
                .orElseThrow(NotExistChannelException::new);
    }

    private Users findUsersById(String id) {
        return usersRepository.findById(id)
                .orElseThrow(NotExistUserException::new);
    }

    private List<Users> findUsersByChannel(Channel channel) {
        return channelUserRepository.findByChannel(channel)
                .stream()
                .map(ChannelUser::getUser)
                .collect(toList());
    }
}
