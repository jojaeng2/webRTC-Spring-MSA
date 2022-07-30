package webrtc.chatservice.repository.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import webrtc.chatservice.domain.*;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.exception.HashTagException.NotExistHashTagException;
import webrtc.chatservice.repository.hashtag.HashTagRepository;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static java.util.concurrent.TimeUnit.SECONDS;

@RequiredArgsConstructor
@Repository
public class ChannelDBRepositoryImpl implements ChannelDBRepository {

    private final int LoadingChannel = 20;

    @PersistenceContext
    private EntityManager em;

    public void save(Channel channel) {
        em.persist(channel);
    }

    public Channel createChannel(Channel channel, List<ChannelHashTag> hashTags) {
        for(ChannelHashTag hashTag : hashTags) {
            channel.addChannelHashTag(hashTag);
        }
        save(channel);
        return channel;
    }

    /*
     * 채널 삭제
     */
    public void deleteChannel(Channel channel) {
        if(channel == null) throw new NotExistChannelException();
        em.remove(channel);
    }



    public void exitChannelUserInChannel(Channel channel, ChannelUser channelUser) {
        channel.exitChannelUser(channelUser);
    }

    /*
     * 모든 채널 불러오기 - 참가자 오름차순
     */
    public List<Channel> findAnyChannelByPartiASC(int idx) {
        return em.createQuery(
                        "select c from Channel c "+
                                "order by c.currentParticipants asc"
                                , Channel.class)
                .setFirstResult(idx * LoadingChannel)
                .setMaxResults(LoadingChannel)
                .getResultList();
    }
    /*
     * 모든 채널 불러오기 - 참가자 내림차순
     */
    public List<Channel> findAnyChannelByPartiDESC(int idx) {
        return em.createQuery(
                        "select c from Channel c "+
                                "order by c.currentParticipants desc"
                        , Channel.class)
                .setFirstResult(idx * LoadingChannel)
                .setMaxResults(LoadingChannel)
                .getResultList();
    }

    /*
     * 내가 입장한 모든 채널 불러오기 - 참가자 오름차순
     */
    public List<Channel> findMyChannelByPartiASC(String userId, int idx) {
        return em.createQuery(
                        "select c from Channel c " +
                                "join c.channelUsers " +
                                "where user_id = :user_id "+
                                "order by c.currentParticipants asc", Channel.class)
                .setParameter("user_id", userId)
                .setFirstResult(idx * LoadingChannel)
                .setMaxResults(LoadingChannel)
                .getResultList();
    }

    /*
     * 내가 입장한 모든 채널 불러오기 - 참가자 내림차순
     */
    public List<Channel> findMyChannelByPartiDESC(String userId, int idx) {
        return em.createQuery(
                        "select c from Channel c " +
                                "join c.channelUsers " +
                                "where user_id = :user_id " +
                                "order by c.currentParticipants desc", Channel.class)
                .setParameter("user_id", userId)
                .setFirstResult(idx * LoadingChannel)
                .setMaxResults(LoadingChannel)
                .getResultList();
    }


    /*
     * 특정 채널을 ID로 찾기
     *
     */
    public Channel findChannelById(String id) {
        Channel channel = em.find(Channel.class, id);
        if(channel == null) throw new NotExistChannelException();
        return channel;
    }

    /*
     * 특정 채널을 channel_id + user_id로 찾기
     */
    public List<Channel> findChannelsByChannelIdAndUserId(String channelId, String userId) {
        List<Channel> channelList = em.createQuery(
                "select c from Channel c " +
                        "join c.channelUsers " +
                        "where user_id = :user_id " +
                        "and c.id = :channel_id", Channel.class)
                .setParameter("channel_id", channelId)
                .setParameter("user_id", userId)
                .getResultList();
        if(channelList.size() == 0) throw new NotExistChannelException();
        return channelList;
    }

    /*
     * 특정 채널을 hashName으로 찾기 - 참가자 오름차순
     *
     */
    public List<Channel> findChannelsByHashNameAndPartiASC(HashTag hashTag, int idx) {
        return em.createQuery(
                "select c from Channel c " +
                        "join c.channelHashTags " +
                        "where hashtag_id = :hashtag_id "+
                        "order by c.currentParticipants asc", Channel.class)
                .setParameter("hashtag_id", hashTag.getId())
                .setFirstResult(idx * LoadingChannel)
                .setMaxResults(LoadingChannel)
                .getResultList();
    }


    /*
     * 특정 채널을 hashName으로 찾기 - 참가자 내림차순
     *
     */
    public List<Channel> findChannelsByHashNameAndPartiDESC(HashTag hashTag, int idx) {
        return em.createQuery(
                        "select c from Channel c " +
                                "join c.channelHashTags " +
                                "where hashtag_id = :hashtag_id "+
                                "order by c.currentParticipants desc", Channel.class)
                .setParameter("hashtag_id", hashTag.getId())
                .setFirstResult(idx * LoadingChannel)
                .setMaxResults(LoadingChannel)
                .getResultList();
    }
    /*
     * 특정 채널을 channelName으로 찾기
     *
     */
    public Channel findChannelByChannelName(String channelName) {
        List<Channel> channels = em.createQuery(
                        "select c from Channel c " +
                                "where c.channelName = :channelName"
                        , Channel.class)
                .setParameter("channelName", channelName)
                .getResultList();
        if(channels.isEmpty()) throw new NotExistChannelException();
        return channels.get(0);
    }
}