package webrtc.v1.chat.repository;

import webrtc.v1.chat.entity.ChatLog;

public interface ChatLogRedisRepository {

    void save(String channelId, ChatLog chatLog);

    void delete(String id);

}
