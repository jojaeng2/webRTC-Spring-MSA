package webrtc.v1.chat.repository;

import org.springframework.stereotype.Repository;
import webrtc.v1.chat.entity.ChatLog;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static java.lang.Math.max;
import static webrtc.v1.chat.enums.ChatLogCount.LOADING;

@Repository
public class ChatLogRepositoryImpl implements ChatLogRepository {

    @PersistenceContext
    private EntityManager em;


    public List<ChatLog> findChatLogsByChannelId(String channelId, Integer idx) {
        return em.createQuery(
            "select cl from ChatLog cl where channel_id = :channel_id and cl.idx BETWEEN :start AND :end", ChatLog.class)
        .setParameter("channel_id", channelId)
        .setParameter("start", max(1, idx-(LOADING.getCount())))
        .setParameter("end", idx-1)
        .getResultList();
    }
}
