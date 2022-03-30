package webrtc.openvidu.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.domain.HashTag;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class HashTagRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(HashTag hashTag) {
        em.persist(hashTag);
    }

    public HashTag findOne(Long id) {
        return em.find(HashTag.class, id);
    }
}
