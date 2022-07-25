package webrtc.chatservice.repository.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import webrtc.chatservice.domain.*;
import webrtc.chatservice.exception.ChannelException;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.exception.HashTagException;
import webrtc.chatservice.exception.HashTagException.NotExistHashTagException;
import webrtc.chatservice.repository.hashtag.HashTagRepository;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static java.util.concurrent.TimeUnit.SECONDS;

@RequiredArgsConstructor
@Repository
public class ChannelRepositoryImpl implements ChannelRepository{

    private final int LoadingChannel = 20;

    @PersistenceContext
    private EntityManager em;

    private final ChannelHashTagRepository channelHashTagRepository;
    private final HashTagRepository hashTagRepository;

    // Redis 설정
    private final RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> opsValueOperation;

    private final Long channelTTL = 10 * 60L;

    /*
     * 초깃값 설정, Test Code에서도 자동으로 실행됨
     */
    @PostConstruct
    private void init() {
        opsValueOperation = redisTemplate.opsForValue();
    }

    /*
     * 채널 생성
     *
     * 서버간 채널 공유를 위해 redis hash에 채널 저장
     * redis에 topic을 만들고 pub/sub 통신을 위해 listener를 설정.
     */

    public void save(Channel channel) {
        em.persist(channel);
    }

    public Channel createChannel(Channel channel, List<String> hashTags) {

        // 채널 생성
        for(String tagName : hashTags) {
            HashTag hashTag;
            try {
                hashTag = hashTagRepository.findHashTagByName(tagName);
            } catch (NotExistHashTagException e) {
                hashTag = new HashTag(tagName);
            }
            ChannelHashTag channelHashTag = new ChannelHashTag();
            channelHashTag.CreateChannelHashTag(channel, hashTag);
            hashTag.addChannelHashTag(channelHashTag);
            channel.addChannelHashTag(channelHashTag);
            channelHashTagRepository.save(channelHashTag);
        }

        // redis 설정
        opsValueOperation.set(channel.getId(), channel);
        redisTemplate.expire(channel.getId(), channelTTL, SECONDS);
        save(channel);
        return channel;
    }

    /*
     * 채널 삭제
     */
    public void deleteChannel(Channel channel) {
        if(channel == null) throw new NotExistChannelException();
        opsValueOperation.getOperations().delete(channel.getId());
        em.remove(channel);
    }

    public void enterChannelUserInChannel(Channel channel, ChannelUser channelUser) {
        channel.enterChannelUser(channelUser);
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
    public List<Channel> findChannelsByHashNameAndPartiASC(String tagName, int idx) {
        HashTag hashTag = hashTagRepository.findHashTagByName(tagName);
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
    public List<Channel> findChannelsByHashNameAndPartiDESC(String tagName, int idx) {
        HashTag hashTag = hashTagRepository.findHashTagByName(tagName);
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


    /*
     * 특정 채널의 TTL 반환
     */
    public Long findChannelTTL(String channelId) {
        return redisTemplate.getExpire(channelId);
    }

    /**
     * 채널의 TTL 연장
     */
    public void extensionChannelTTL(Channel channel, Long addTTL) {
        Long newTTL = findChannelTTL(channel.getId()) + addTTL;
        channel.setTimeToLive(newTTL);
        redisTemplate.expire(channel.getId(), newTTL, SECONDS);
    }

    /*
     * 특정 채널의 현재 참가자 수 반환
     */
    public Long getCurrentParticipants(Channel channel) {
        return channel.getCurrentParticipants();
    }



}
