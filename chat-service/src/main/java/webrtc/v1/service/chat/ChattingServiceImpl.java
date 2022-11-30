package webrtc.v1.service.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.domain.Channel;
import webrtc.v1.domain.ChannelUser;
import webrtc.v1.domain.Users;
import webrtc.v1.dto.chat.ChattingMessage;
import webrtc.v1.enums.ClientMessageType;
import webrtc.v1.exception.ChannelException.NotExistChannelException;
import webrtc.v1.repository.channel.ChannelCrudRepository;
import webrtc.v1.repository.users.ChannelUserRepository;
import webrtc.v1.service.chat.factory.ChattingMessageFactory;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static webrtc.v1.enums.ClientMessageType.REENTER;

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
        redisTemplate.convertAndSend(channelTopic.getTopic(), serverMessage);
    }

}
