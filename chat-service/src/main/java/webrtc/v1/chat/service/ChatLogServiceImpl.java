package webrtc.v1.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.chat.entity.ChatLog;
import webrtc.v1.chat.repository.ChatLogRedisRepository;
import webrtc.v1.user.entity.Users;
import webrtc.v1.chat.enums.ClientMessageType;
import webrtc.v1.chat.repository.ChatLogRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatLogServiceImpl implements ChatLogService {

    private final ChatLogRepository chatLogRepository;
    private final ChatLogRedisRepository redisRepository;

    public long save(ClientMessageType type, String chatMessage, Channel channel, Users user) {
        ChatLog chatLog = ChatLog.builder()
                .type(type)
                .message(chatMessage)
                .senderNickname(user.getNickname())
                .senderEmail(user.getEmail())
                .build();
        Integer index = redisRepository.findLastIndex(channel.getId());
        chatLog.setChatLogIdx(index + 1);
        redisRepository.addLastIndex(channel.getId());
        channel.addChatLog(chatLog);
        return chatLog.getIdx();
    }

    @Transactional(readOnly = true)
    public List<ChatLog> findChatLogsByIndex(String channelId, int idx) {
        return chatLogRepository.findChatLogsByChannelId(channelId, idx);
    }

    @Transactional(readOnly = true)
    public int findLastIndexByChannelId(String id) {
        return redisRepository.findLastIndex(id);
    }
}
