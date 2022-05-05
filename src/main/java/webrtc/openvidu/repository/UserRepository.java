package webrtc.openvidu.repository;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void saveUser(User user) {
        em.persist(user);
    }

    public List<User> findUsersByName(String name) {
        return em.createQuery(
                "select u from User u " +
                        "where u.nickname = :nickname"
                , User.class)
                .setParameter("nickname", name)
                .getResultList();
    }

    public void deleteChannel(Channel channel, User user) {
        String userId = user.getId();
        em.createQuery(
                "delete "
        );
    }

    public List<User> findUsersByChannelId(String channelId) {
        return em.createQuery(
                "select u from User u " +
                        "join u.channelUsers " +
                        "where channel_id = :channel_id", User.class)
                .setParameter("channel_id", channelId)
                .getResultList();
    }

}
