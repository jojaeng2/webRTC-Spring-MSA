package webrtc.openvidu.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.domain.ChannelHashTag;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
@RequiredArgsConstructor
public class ChannelHashTagRepository {

    @PersistenceContext
    private final EntityManager em;

    @Transactional
    public void save(ChannelHashTag channelHashTag) {
        em.persist(channelHashTag);
    }
}
