package webrtc.openvidu.repository.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.domain.channel.Channel;
import webrtc.openvidu.service.pubsub.RedisSubscriber;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class ChannelRepository {

    // 채널(==topic)에 발행되는 메시지 처리
    private final RedisMessageListenerContainer redisMessageListenerContainer;

    // topic 구독 처리 서비스
    private final RedisSubscriber redisSubscriber;

    // Redis 설정
    private static final String TOPIC = "TOPIC";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, Channel> opsHashChannel;

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
        opsHashChannel = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }

    /*
     * 채널 생성
     *
     * 서버간 채널 공유를 위해 redis hash에 채널 저장
     * redis에 topic을 만들고 pub/sub 통신을 위해 listener를 설정.
     */
    public Channel createChannel(Channel channel) {
        opsHashChannel.put(TOPIC, channel.getId(), channel);
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
        opsHashChannel.delete(TOPIC, channel.getId());
    }

    /*
     * 채널 업데이트
     */
    public Channel updateChannel(Channel channel) {
        opsHashChannel.put(TOPIC, channel.getId(), channel);
        return channel;
    }

    /*
    * 유저 채널 입장
    */
    public Channel enterChannel(Channel channel, User user) {
        channel.addUser(user);
        return channel;
    }

    /**
     * 유저 채널 퇴장
     */
    public Channel leaveChannel(Channel channel, User user) {
        channel.delUser(user);
        return channel;
    }

    /*
     * 모든 채널 불러오기
     */
    public List<Channel> findAllChannel() {
        return opsHashChannel.values(TOPIC);
    }

    /*
     * 특정 채널을 ID로 찾기
     *
     */
    public Channel findOneChannelById(String id) {
        return opsHashChannel.get(TOPIC, id);
    }

    /*
     * 특정 topic 찾기
     *
     */
    public ChannelTopic getTopic(String channelId) {
        return topics.get(channelId);
    }



}
