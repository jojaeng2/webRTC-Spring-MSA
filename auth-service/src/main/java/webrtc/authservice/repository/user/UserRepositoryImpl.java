package webrtc.authservice.repository.user;

import org.springframework.stereotype.Repository;
import webrtc.authservice.domain.Users;
import webrtc.authservice.exception.UserException.NotExistUserException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Users user) {
        em.persist(user);
    }

    public Users findUserByEmail(String email) {
        List<Users> users = em.createQuery(
                        "select u from Users u " +
                                "where u.email = :email"
                        , Users.class)
                .setParameter("email", email)
                .getResultList();
        if(users.size() == 0) throw new NotExistUserException();
        return users.get(0);
    }
}
