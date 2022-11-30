package webrtc.chatservice.repository.voice;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import webrtc.chatservice.domain.VoiceRoom;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class VoiceRoomRepositoryImpl implements VoiceRoomRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> opsValueOperation;

    @PostConstruct
    private void init() {
        opsValueOperation = redisTemplate.opsForValue();
    }

    public void save(String sessionName, VoiceRoom voiceRoom) {
        opsValueOperation.set(sessionName, voiceRoom);
    }

    public Optional<VoiceRoom> findOpenViduSessionByName(String sessionName) {
        return Optional.ofNullable((VoiceRoom) opsValueOperation.get(sessionName));
    }

    public void update(String sessionName, VoiceRoom voiceRoom) {
        opsValueOperation.set(sessionName, voiceRoom);
    }

    public void delete(String sesionName) {
        opsValueOperation.set(sesionName, "", 1, TimeUnit.MILLISECONDS);
    }

}
