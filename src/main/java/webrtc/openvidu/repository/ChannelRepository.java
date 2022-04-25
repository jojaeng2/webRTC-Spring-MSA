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
    private final UserRepository userRepository;

    // 채널(topic)에 발행되는 메시지 처리
    private final RedisMessageListenerContainer redisMessageListenerContainer;

    // topic 구독 처리 서비스
    private final RedisSubscriber redisSubscriber;


    // Redis 설정
    private final RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> opsValueOperation;


    /*
     * 채널에 메시지를 발행하기 위한 redis topic 정보
     * 서버별로 topic에 매치되는 topic 정보를 Map에 넣어 관리
     */
    private static Map<String, ChannelTopic> topics;

    /*
     * 초깃값 설정, Test Code에서도 자동으로 실행됨
     */
    @PostConstruct
    private void init() {
        opsValueOperation = redisTemplate.opsForValue();
        topics = new HashMap<>();
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

        // message subscriber를 위해 redisMessageListenerContainer 추가
        ChannelTopic topic = topics.get(channel.getId());
        if(topic == null) {
            topic = new ChannelTopic(channel.getId());
            redisMessageListenerContainer.addMessageListener(redisSubscriber, topic);
            topics.put(channel.getId(), topic);
        }

        return channel;
    }

    /*
     * 채널 삭제
     */
    public void deleteChannel(Channel channel) {
        ChannelTopic topic = topics.get(channel.getId());
        redisMessageListenerContainer.removeMessageListener(redisSubscriber, topic);
        topics.remove(channel.getId());
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


    /*
     * 특정 topic 찾기
     *
     */
    public ChannelTopic getTopic(String channelId) {
        return topics.get(channelId);
    }



}
