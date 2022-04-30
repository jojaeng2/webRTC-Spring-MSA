package webrtc.openvidu.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.domain.ChannelUser;
import webrtc.openvidu.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class ChannelUserRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void enterChannel(Channel channel, User user) {
        ChannelUser channelUser = new ChannelUser(channel, user);
        em.persist(channelUser);
    }

    @Transactional
    public void exitChannel(String channelId, String userId) {
        em.createQuery(
                "delete from ChannelUser cu " +
                "where channel_id = :channel_id " +
                "and user_id = :user_id")
                .setParameter("channel_id", channelId)
                .setParameter("user_id", userId).executeUpdate();
    }


}
