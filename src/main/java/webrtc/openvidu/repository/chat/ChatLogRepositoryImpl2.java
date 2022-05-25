package webrtc.openvidu.repository.chat;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.domain.ChatLog;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static java.lang.Math.max;

@Repository
public class ChatLogRepositoryImpl2 implements ChatLogRepository {
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
        return em.createQuery(
                    "select cl from ChatLog cl " +
                            "where channel_id = :channel_id " +
                            "and cl.idx BETWEEN :start AND :end"
                    , ChatLog.class)
                .setParameter("channel_id", channelId)
                .setParameter("start", max(1L, idx-(LoadingChatCount)))
                .setParameter("end", idx-1L)
                .getResultList();
    }

    public List<ChatLog> findLastChatLogsByChannelId(String channelId) {
        return em.createQuery(
                "select cl from ChatLog cl " +
                        "where channel_id = :channel_id " +
                        "order by sendTime DESC ", ChatLog.class
                ).setParameter("channel_id", channelId)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();
    }
}
