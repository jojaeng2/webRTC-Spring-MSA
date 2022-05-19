package webrtc.openvidu.repository.chat;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.domain.ChatLog;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ChatRepositoryImpl implements ChatRepository {

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

    public List<ChatLog> findTenChatLogsByChannelId(String channelId, int idx) {
        return em.createQuery(
                "select cl from ChatLog cl " +
                        "where channel_id = :channel_id " +
                        "order by sendTime DESC ", ChatLog.class).
                setParameter("channel_id", channelId)
                .setFirstResult(idx*10)
                .setMaxResults(10)
                .getResultList();
    }
}
