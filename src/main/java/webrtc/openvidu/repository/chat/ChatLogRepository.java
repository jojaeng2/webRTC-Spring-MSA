package webrtc.openvidu.repository.chat;

import webrtc.openvidu.domain.ChatLog;

import java.util.List;

public interface ChatLogRepository {

    void save(ChatLog chatLog);

    List<ChatLog> findAllChatLogsByChannelId();
}
