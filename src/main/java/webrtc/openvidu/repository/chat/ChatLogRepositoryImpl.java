package webrtc.openvidu.repository.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.domain.ChatLog;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ChatLogRepositoryImpl implements ChatLogRepository{

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(ChatLog chatLog) {
        em.persist(chatLog);
    }

    public List<ChatLog> findAllChatLogsByChannelId() {
        return em.createQuery(
                "select cl from ChatLog cl "
                , ChatLog.class)
                .getResultList();
    }
}
