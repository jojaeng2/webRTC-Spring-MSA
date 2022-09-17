package webrtc.chatservice.repository.channel;

import lombok.RequiredArgsConstructor;
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

//    public void save(Channel channel) {
//        em.persist(channel);
//    }

//    public Channel create(Channel channel, List<ChannelHashTag> hashTags) {
//        hashTags.forEach(channel::addChannelHashTag);
//        save(channel);
//        return channel;
//    }

    /*
     * 채널 삭제
     */
//    public void delete(Channel channel) {
//        if(channel == null) throw new NotExistChannelException();
//        em.remove(channel);
//    }



    public void exitChannelUserInChannel(Channel channel, ChannelUser channelUser) {
        channel.exitChannelUser(channelUser);
    }

    public List<Channel> findAnyChannels(int idx, String type) {
        return em.createQuery(
                        "select c from Channel c "+
                                "order by c.currentParticipants " + type
                        , Channel.class)
                .setFirstResult(idx * LoadingChannel)
                .setMaxResults(LoadingChannel)
                .getResultList();
    }

    public List<Channel> findMyChannels(String userId, int idx, String type) {
        return em.createQuery(
                        "select c from Channel c " +
                                "join c.channelUsers " +
                                "where user_id = :user_id " +
                                "order by c.currentParticipants " + type, Channel.class)
                .setParameter("user_id", userId)
                .setFirstResult(idx * LoadingChannel)
                .setMaxResults(LoadingChannel)
                .getResultList();
    }


    /*
     * 특정 채널을 ID로 찾기
     *
     */
//    public Channel findChannelById(String id) {
//        Channel channel = em.find(Channel.class, id);
//        if(channel == null) throw new NotExistChannelException();
//        return channel;
//    }

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

    public List<Channel> findChannelsByHashName(HashTag hashTag, int idx, String type) {
        System.out.println("hashTag.getName() = " + hashTag.getTagName());
        return em.createQuery(
                "select c from Channel c " +
                        "join c.channelHashTags " +
                        "where hashtag_id = :hashtag_id "+
                        "order by c.currentParticipants " + type, Channel.class)
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
