package webrtc.chatservice.repository.chat;

import org.springframework.stereotype.Repository;
import webrtc.chatservice.domain.ChatLog;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static java.lang.Math.max;

@Repository
public class ChatLogRepositoryImpl implements ChatLogRepository {

    private final int LoadingChatCount = 20;

    @PersistenceContext
    private EntityManager em;

    public void save(ChatLog chatLog) {
        em.persist(chatLog);
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
                        "order by idx desc ", ChatLog.class
                ).setParameter("channel_id", channelId)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();
    }
}
