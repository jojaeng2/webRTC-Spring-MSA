package webrtc.chatservice.repository.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import webrtc.chatservice.domain.ChannelHashTag;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
@RequiredArgsConstructor
public class ChannelHashTagRepositoryImpl implements ChannelHashTagRepository{

    @PersistenceContext
    private final EntityManager em;

    public void save(ChannelHashTag channelHashTag) {
        em.persist(channelHashTag);
    }
}
