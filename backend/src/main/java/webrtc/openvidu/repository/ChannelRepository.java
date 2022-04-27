package webrtc.openvidu.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;
import webrtc.openvidu.domain.*;
import webrtc.openvidu.dto.ChannelDto.CreateChannelRequest;
import webrtc.openvidu.service.pubsub.RedisSubscriber;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class ChannelRepository {

    @PersistenceContext
    private EntityManager em;

    private final ChannelHashTagRepository channelHashTagRepository;
    private final HashTagRepository hashTagRepository;

    // Redis 설정
    private final RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> opsValueOperation;

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
    public Channel createChannel(CreateChannelRequest request) {
        // 변수
        String channelName = request.getChannelName();

        // 채널 생성
        Channel channel = new Channel(channelName);
        List<String> hashTags = request.getHashTags();
        for(String tagName : hashTags) {
            HashTag hashTag;
            HashTag tags = hashTagRepository.findHashTagByName(tagName);
            if(tags == null) hashTag = new HashTag(tagName);
            else hashTag = tags;
            ChannelHashTag channelHashTag = new ChannelHashTag();
            channelHashTag.CreateChannelHashTag(channel, hashTag);
            hashTag.addChannelHashTag(channelHashTag);
            channel.addChannelHashTag(channelHashTag);
            channelHashTagRepository.save(channelHashTag);
        }

        // redis 설정
        opsValueOperation.set(channel.getId(), channel);
        redisTemplate.expire(channel.getId(), 24, TimeUnit.HOURS);

        return channel;
    }

    /*
     * 채널 삭제
     */
    public void deleteChannel(Channel channel) {
        opsValueOperation.getOperations().delete(channel.getId());
        em.remove(channel);
    }

    /*
     * 채널 업데이트
     */
    public Channel updateChannel(Channel channel) {
        String channelId = channel.getId();
        opsValueOperation.set(channelId, channel, redisTemplate.getExpire(channelId));
//        em.persist(channel);
        return channel;
    }

    /*
     * 모든 채널 불러오기
     */
    public List<Channel> findAllChannel() {
        List<Channel> channels = new ArrayList<>();
        Set<String> keys = redisTemplate.keys("*");
        Iterator<String> iter = keys.iterator();
        while(iter.hasNext()) {
            String channelId = iter.next();
            Channel channel = (Channel) opsValueOperation.get(channelId);
            System.out.println(channelId);
            channels.add(channel);
        }
        return channels;
    }

    /*
     * 특정 채널을 ID로 찾기
     *
     */
    public Channel findOneChannelById(String id) {
        return (Channel) opsValueOperation.get(id);
    }

    /*
     * 특정 채널을 user_id로 찾기
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
    public List<Channel> findOneChannelByHashName(String tagName) {
        HashTag hashTag = hashTagRepository.findHashTagByName(tagName);
        return em.createQuery(
                "select c from Channel c " +
                        "join c.channelHashTags " +
                        "where hashtag_id = :hashtag_id", Channel.class)
                .setParameter("hashtag_id", hashTag.getId())
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

    /*
     * 특정 채널의 현재 참가자 수 갱신
     */
    public Long updateCurrentParticipants(Channel channel) {
        return channel.getCurrentParticipants();
    }





}
