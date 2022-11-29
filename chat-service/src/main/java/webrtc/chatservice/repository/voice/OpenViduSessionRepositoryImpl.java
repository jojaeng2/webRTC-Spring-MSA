package webrtc.chatservice.repository.voice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import webrtc.chatservice.domain.VoiceRoom;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class OpenViduSessionRepositoryImpl implements OpenViduSessionRepository{

    // Redis 설정
    private final RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> opsValueOperation;

    @PostConstruct
    private void init() {
        opsValueOperation = redisTemplate.opsForValue();
    }

    public void createSession(String sessionName, VoiceRoom voiceRoom) {
        opsValueOperation.set(sessionName, voiceRoom);
    }

    public VoiceRoom findOpenViduSessionByName(String sessionName) {
        return VoiceRoom.class.cast(opsValueOperation.get(sessionName));
    }

    public void deletedChannel(String channelId) {
        Object obj = opsValueOperation.get(channelId);
        if(obj != null) {
            delete(channelId);
        }
    }

    public void update(String sessionName, VoiceRoom voiceRoom) {
        opsValueOperation.set(sessionName, voiceRoom);
    }

    public void delete(String sesionName) {
        opsValueOperation.set(sesionName, "", 1, TimeUnit.MILLISECONDS);
    }

}
