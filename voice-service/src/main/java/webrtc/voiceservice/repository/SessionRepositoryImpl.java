package webrtc.voiceservice.repository;

import io.openvidu.java.client.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import webrtc.voiceservice.domain.User;

import javax.annotation.PostConstruct;

@Repository
@RequiredArgsConstructor
public class SessionRepositoryImpl {

    private final RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> opsHashSession;

    @PostConstruct
    public void init() {
        opsHashSession = redisTemplate.opsForValue();
    }


}
