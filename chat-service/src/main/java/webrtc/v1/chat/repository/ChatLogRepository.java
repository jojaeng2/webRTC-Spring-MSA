package webrtc.v1.chat.repository;

import webrtc.v1.chat.entity.ChatLog;

import java.util.List;

public interface ChatLogRepository {


    List<ChatLog> findChatLogsByChannelId(String channelId, Long idx);

    List<ChatLog> findLastChatLogsByChannelId(String channelId);
}
