package webrtc.v1.repository.chat;

import webrtc.v1.domain.ChatLog;

import java.util.List;

public interface ChatLogRepository {


    List<ChatLog> findChatLogsByChannelId(String channelId, Long idx);

    List<ChatLog> findLastChatLogsByChannelId(String channelId);
}
