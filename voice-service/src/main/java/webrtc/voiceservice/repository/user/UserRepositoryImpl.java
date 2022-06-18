package webrtc.voiceservice.repository.user;

import org.springframework.stereotype.Repository;
import webrtc.voiceservice.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository{

    @PersistenceContext
    private EntityManager em;

    public List<User> findUsersByEmail(String email) {
        return em.createQuery(
                        "select u from User u " +
                                "where u.email = :email"
                        , User.class)
                .setParameter("email", email)
                .getResultList();
    }


}
