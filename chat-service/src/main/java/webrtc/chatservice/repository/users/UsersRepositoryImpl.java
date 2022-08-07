package webrtc.chatservice.repository.users;

import org.springframework.stereotype.Repository;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.exception.UserException.NotExistUserException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UsersRepositoryImpl implements UsersRepository {

    @PersistenceContext
    private EntityManager em;

    public void saveUser(Users users) {
        em.persist(users);
    }

    public Users findUserByEmail(String email) {
        List<Users> usersList = em.createQuery(
                "select u from Users u " +
                        "where u.email = :email"
                , Users.class)
                .setParameter("email", email)
                .getResultList();
        if(usersList.size() == 0) throw new NotExistUserException();
        return usersList.get(0);
    }


    public List<Users> findUsersByChannelId(String channelId) {
        return em.createQuery(
                "select u from Users u " +
                        "join u.channelUsers " +
                        "where channel_id = :channel_id", Users.class)
                .setParameter("channel_id", channelId)
                .getResultList();
    }

}
