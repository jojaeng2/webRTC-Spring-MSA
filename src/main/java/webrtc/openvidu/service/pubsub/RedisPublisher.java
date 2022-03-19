package webrtc.openvidu.service.pubsub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import webrtc.openvidu.domain.chat.dto.ServerMessage;

@RequiredArgsConstructor
@Service
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChannelTopic topic, ServerMessage message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
