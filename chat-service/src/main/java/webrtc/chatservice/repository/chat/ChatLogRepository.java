package webrtc.chatservice.repository.chat;

import webrtc.chatservice.domain.ChatLog;

import java.util.List;

public interface ChatLogRepository {

    void save(ChatLog chatLog);

    List<ChatLog> findAllChatLogsByChannelId(String channelId);

    List<ChatLog> findChatLogsByChannelId(String channelId, Long idx);

    List<ChatLog> findLastChatLogsByChannelId(String channelId);
}
