package webrtc.chatservice.repository.user;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.exception.UserException;
import webrtc.chatservice.exception.UserException.NotExistUserException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository{

    @PersistenceContext
    private EntityManager em;

    public void saveUser(User user) {
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


    public List<User> findUsersByChannelId(String channelId) {
        return em.createQuery(
                "select u from User u " +
                        "join u.channelUsers " +
                        "where channel_id = :channel_id", User.class)
                .setParameter("channel_id", channelId)
                .getResultList();
    }

    public void setChannelUser(User user, ChannelUser channelUser) {
        User findUser = em.find(User.class, user.getId());
        ChannelUser findChannelUser = em.find(ChannelUser.class, channelUser.getId());
        findUser.addChannelUser(findChannelUser);
    }

}
