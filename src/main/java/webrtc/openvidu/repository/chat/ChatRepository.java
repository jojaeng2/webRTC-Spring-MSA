package webrtc.openvidu.repository.chat;

import webrtc.openvidu.domain.ChatLog;

import java.util.List;

public interface ChatRepository {

    void save(ChatLog chatLog);

    List<ChatLog> findAllChatLogsByChannelId(String channelId);

    List<ChatLog> findTenChatLogsByChannelId(String channelId, int idx);
}
