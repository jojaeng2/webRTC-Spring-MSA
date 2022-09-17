package webrtc.chatservice.repository.chat;

import io.lettuce.core.dynamic.annotation.Param;
import webrtc.chatservice.domain.ChatLog;

import java.util.List;

public interface ChatLogRepository {

    void save(ChatLog chatLog);

    List<ChatLog> findChatLogsByChannelId(@Param("channel_id") String channel_id, Long idx);

    List<ChatLog> findLastChatLogsByChannelId(@Param("channel_id") String channel_id);
}
