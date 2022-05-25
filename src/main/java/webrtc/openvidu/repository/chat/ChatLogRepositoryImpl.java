package webrtc.openvidu.repository.chat;

import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.domain.ChatLog;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

public class ChatLogRepositoryImpl implements ChatLogRepository {

    private final int LoadingChatCount = 20;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(ChatLog chatLog) {
        em.persist(chatLog);
    }

    public List<ChatLog> findAllChatLogsByChannelId(String channelId) {
        return em.createQuery(
                "select cl from ChatLog cl " +
                        "where channel_id=:channel_id", ChatLog.class)
                .setParameter("channel_id", channelId)
                .getResultList();
    }

    public List<ChatLog> findChatLogsByChannelId(String channelId, Long idx) {
        return new ArrayList<ChatLog>();
    }

    public List<ChatLog> findLastChatLogsByChannelId(String channelId) {
        return new ArrayList<ChatLog>();
    }
}
