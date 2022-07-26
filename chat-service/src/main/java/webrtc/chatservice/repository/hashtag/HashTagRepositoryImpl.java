package webrtc.chatservice.repository.hashtag;

import org.springframework.stereotype.Repository;
import webrtc.chatservice.domain.HashTag;
import webrtc.chatservice.exception.HashTagException;
import webrtc.chatservice.exception.HashTagException.NotExistHashTagException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class HashTagRepositoryImpl implements HashTagRepository{

    @PersistenceContext
    private EntityManager em;

    public void save(HashTag hashTag) {
        em.persist(hashTag);
    }

    public HashTag findHashTagByName(String tagName) {
        List<HashTag> hashTagList = em.createQuery(
                "select h from HashTag h where h.tagName = :tagName", HashTag.class)
                .setParameter("tagName", tagName)
                .getResultList();
        if(hashTagList.isEmpty()) throw new NotExistHashTagException();
        return hashTagList.get(0);
    }
}
