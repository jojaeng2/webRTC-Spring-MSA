package webrtc.openvidu.repository.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.domain.*;
import webrtc.openvidu.repository.hashtag.HashTagRepository;
import webrtc.openvidu.repository.hashtag.HashTagRepositoryImpl;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    private final Long channelTTL = 1 * 10L;

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

    @Transactional
    public void save(Channel channel) {
        em.persist(channel);
    }

    @Transactional
    public Channel createChannel(Channel channel, List<String> hashTags) {

        // 채널 생성
        for(String tagName : hashTags) {
            HashTag hashTag;
            List<HashTag> tags = hashTagRepository.findHashTagByName(tagName);
            if(tags.isEmpty()) hashTag = new HashTag(tagName);
            else hashTag = tags.get(0);
            ChannelHashTag channelHashTag = new ChannelHashTag();
            channelHashTag.CreateChannelHashTag(channel, hashTag);
            hashTag.addChannelHashTag(channelHashTag);
            channel.addChannelHashTag(channelHashTag);
            channelHashTagRepository.save(channelHashTag);
        }

        // redis 설정
        opsValueOperation.set(channel.getId(), channel);
        redisTemplate.expire(channel.getId(), channelTTL, TimeUnit.SECONDS);

        return channel;
    }

    /*
     * 채널 삭제
     */
    @Transactional
    public void deleteChannel(Channel channel) {
        opsValueOperation.getOperations().delete(channel.getId());
        Channel deleteChannel = em.find(Channel.class, channel.getId());
        em.remove(deleteChannel);
    }

    /*
     * 채널 업데이트
     */
    public Channel updateChannel(Channel channel) {
        String channelId = channel.getId();
        channel.setTimeToLive(redisTemplate.getExpire(channelId));
//        em.persist(channel);
        return channel;
    }

    /*
     * 모든 채널 불러오기
     */
    public List<Channel> findAnyChannel(int idx) {
        return em.createQuery(
                        "select c from Channel c "
                                , Channel.class)
                .setFirstResult(idx * LoadingChannel)
                .setMaxResults(LoadingChannel)
                .getResultList();
    }

    /*
     * 모든 채널 불러오기
     */
    public List<Channel> findMyChannel(String userId, int idx) {
        return em.createQuery(
                        "select c from Channel c " +
                                "join c.channelUsers " +
                                "where user_id = :user_id ", Channel.class)
                .setParameter("user_id", userId)
                .setFirstResult(idx * LoadingChannel)
                .setMaxResults(LoadingChannel)
                .getResultList();
    }


    /*
     * 특정 채널을 ID로 찾기
     *
     */
    public List<Channel> findChannelsById(String id) {
        return em.createQuery(
                        "select c from Channel c " +
                                "where c.id = :id"
                        , Channel.class)
                .setParameter("id", id)
                .getResultList();
    }

    /*
     * 특정 채널을 channel_id + user_id로 찾기
     */
    public List<Channel> findChannelsByUserId(String channelId, String userId) {
        return em.createQuery(
                "select c from Channel c " +
                        "join c.channelUsers " +
                        "where user_id = :user_id " +
                        "and c.id = :channel_id", Channel.class)
                .setParameter("channel_id", channelId)
                .setParameter("user_id", userId)
                .getResultList();
    }

    /*
     * 특정 채널을 hashName으로 찾기
     *
     */
    public List<Channel> findChannelsByHashName(String tagName) {
        List<HashTag> hashTags = hashTagRepository.findHashTagByName(tagName);
        return em.createQuery(
                "select c from Channel c " +
                        "join c.channelHashTags " +
                        "where hashtag_id = :hashtag_id", Channel.class)
                .setParameter("hashtag_id", hashTags.get(0).getId())
                .getResultList();
    }
    /*
     * 특정 채널을 channelName으로 찾기
     *
     */
    public List<Channel> findChannelsByChannelName(String channelName) {
        return em.createQuery(
                "select c from Channel c " +
                "where c.channelName = :channelName"
                , Channel.class)
                .setParameter("channelName", channelName)
                .getResultList();
    }


    /*
     * 특정 채널의 TTL 반환
     */
    public Long findChannelTTL(String channelId) {
        return redisTemplate.getExpire(channelId);
    }

    /*
     * 특정 채널의 현재 참가자 수 반환
     */
    public Long getCurrentParticipants(Channel channel) {
        return channel.getCurrentParticipants();
    }



}
