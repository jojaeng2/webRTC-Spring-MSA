package webrtc.v1.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.chat.entity.ChatLog;
import webrtc.v1.chat.enums.ClientMessageType;
import webrtc.v1.chat.repository.ChatLogRedisRepositoryImpl;
import webrtc.v1.user.entity.Users;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static webrtc.v1.chat.enums.ChatLogCount.LOADING;

@Service
@RequiredArgsConstructor
public class ChatLogServiceImpl implements ChatLogService {

    private final ChatLogRedisRepositoryImpl redisRepository;

    public long save(ClientMessageType type, String chatMessage, Channel channel, Users user) {
        ChatLog chatLog = ChatLog.builder()
                .type(type)
                .message(chatMessage)
                .senderNickname(user.getNickname())
                .senderEmail(user.getEmail())
                .build();
        Integer index = redisRepository.findLastIndex(channel.getId()).orElse(0);
        chatLog.setChatLogIdx(index);
        redisRepository.save(channel.getId(), chatLog);
        redisRepository.addLastIndex(channel.getId());
        channel.addChatLog(chatLog);
        return chatLog.getIdx();
    }

    @Transactional(readOnly = true)
    public List<ChatLog> findChatLogsByIndex(String channelId, int idx) {
        List<Optional<ChatLog>> chatLogs = new ArrayList<>();
        IntStream.range(getRange(idx), idx).forEach(
                num -> chatLogs.add(redisRepository.findByChannelIdAndIndex(channelId, num))
        );
        return chatLogs.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public int findLastIndexByChannelId(String id) {
        return redisRepository.findLastIndex(id).orElse(0);
    }

    private int getRange(int index) {
        return Math.max(0, index - (LOADING.getCount()));
    }
}
