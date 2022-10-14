package webrtc.chatservice.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChatLog;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.repository.chat.ChatLogRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatLogServiceImpl implements ChatLogService{

    private final ChatLogRepository chatLogRepository;

    public long saveChatLog(ClientMessageType type, String chatMessage, Channel channel, Users user) {
        List<ChatLog> findChatLogs = chatLogRepository.findLastChatLogsByChannelId(channel.getId());
        ChatLog chatLog = ChatLog.builder()
                .type(type)
                .message(chatMessage)
                .senderNickname(user.getNickname())
                .senderEmail(user.getEmail())
                .build();

        if(findChatLogs.isEmpty()) chatLog.setChatLogIdx(1L);
        else chatLog.setChatLogIdx(findChatLogs.get(0).getIdx() + 1);

        channel.addChatLog(chatLog);
        return chatLog.getIdx();
    }

    @Transactional(readOnly = true)
    public List<ChatLog> findChatLogsByIndex(String channelId, Long idx) {
        return chatLogRepository.findChatLogsByChannelId(channelId, idx);
    }

    @Transactional(readOnly = true)
    public ChatLog findLastChatLogsByChannelId(String channelId) {
        return chatLogRepository.findLastChatLogsByChannelId(channelId).get(0);
    }
}
