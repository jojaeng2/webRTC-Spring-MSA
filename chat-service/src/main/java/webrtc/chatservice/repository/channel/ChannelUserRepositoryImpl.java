package webrtc.chatservice.repository.channel;

import org.springframework.stereotype.Repository;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.exception.ChannelUserException.NotExistChannelUserException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ChannelUserRepositoryImpl implements ChannelUserRepository{

    @PersistenceContext
    private EntityManager em;


    public ChannelUser findOneChannelUser(String channelId, String userId) {
        List<ChannelUser> channelUserList = em.createQuery(
                        "select cu from ChannelUser cu " +
                                "where channel_id = :channel_id " +
                                "and user_id = :user_id", ChannelUser.class)
                .setParameter("channel_id", channelId)
                .setParameter("user_id", userId)
                .getResultList();
        if(channelUserList.isEmpty()) throw new NotExistChannelUserException();
        return channelUserList.get(0);
    }


}
