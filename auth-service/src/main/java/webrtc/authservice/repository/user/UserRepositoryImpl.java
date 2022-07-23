package webrtc.authservice.repository.user;

import org.springframework.stereotype.Repository;
import webrtc.authservice.domain.User;
import webrtc.authservice.exception.UserException.NotExistUserException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(User user) {
        em.persist(user);
    }

    public User findUserByEmail(String email) {
        List<User> users = em.createQuery(
                        "select u from User u " +
                                "where u.email = :email"
                        , User.class)
                .setParameter("email", email)
                .getResultList();
        if(users.size() == 0) throw new NotExistUserException();
        return users.get(0);
    }
}
