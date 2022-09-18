package webrtc.chatservice.repository.channel;

import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import webrtc.chatservice.domain.*;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@RequiredArgsConstructor
@Repository
public class ChannelListRepositoryImpl implements ChannelListRepository {

    private final int LoadingChannel = 20;

    @PersistenceContext
    private EntityManager em;


    /*
     * 특정 채널을 channelName으로 찾기
     *
     */
    public List<Channel> findChannelByChannelName(String channelName) {
        return em.createQuery("select c from Channel c where c.channelName = :channelName", Channel.class)
                .setParameter("channelName", channelName)
                .getResultList();
    }


    /*
     * 특정 채널을 channel_id + user_id로 찾기
     */
    public List<Channel> findChannelsByChannelIdAndUserId(String channelId, String userId) {
        return em.createQuery("select c from Channel c join c.channelUsers where user_id = :user_id and c.id = :channel_id", Channel.class)
                .setParameter("channel_id", channelId)
                .setParameter("user_id", userId)
                .getResultList();
    }

    public List<Channel> findAnyChannels(int idx, String type) {
        return em.createQuery("select c from Channel c order by c.currentParticipants " + type, Channel.class)
                .setFirstResult(idx * LoadingChannel)
                .setMaxResults(LoadingChannel)
                .getResultList();
    }

    public List<Channel> findMyChannels(String userId, int idx, String type) {
        return em.createQuery("select c from Channel c join c.channelUsers where user_id = :user_id order by c.currentParticipants " + type, Channel.class)
                .setParameter("user_id", userId)
                .setFirstResult(idx * LoadingChannel)
                .setMaxResults(LoadingChannel)
                .getResultList();
    }


    public List<Channel> findChannelsByHashName(HashTag hashTag, int idx, String type) {
        return em.createQuery(
                "select c from Channel c join c.channelHashTags where hashtag_id = :hashtag_id order by c.currentParticipants " + type, Channel.class)
                .setParameter("hashtag_id", hashTag.getId())
                .setFirstResult(idx * LoadingChannel)
                .setMaxResults(LoadingChannel)
                .getResultList();
    }

}
