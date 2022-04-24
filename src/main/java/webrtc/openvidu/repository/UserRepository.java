package webrtc.openvidu.repository;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Getter
public class UserRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void saveUser(User user) {
        em.persist(user);
    }

    public List<User> findUserByName(String name) {
        return em.createQuery(
                "select u from User u " +
                        "where u.nickname = :nickname"
                , User.class)
                .setParameter("nickname", name)
                .getResultList();
    }

}
