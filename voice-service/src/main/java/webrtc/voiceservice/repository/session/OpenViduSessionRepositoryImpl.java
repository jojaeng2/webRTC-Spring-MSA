package webrtc.voiceservice.repository.session;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import webrtc.voiceservice.domain.OpenViduSession;

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

    public void createSession(String sessionName, OpenViduSession openViduSession) {
        opsValueOperation.set(sessionName, openViduSession);
    }

    public OpenViduSession findOpenViduSessionByName(String sessionName) {
        return OpenViduSession.class.cast(opsValueOperation.get(sessionName));
    }

    public void update(String sessionName, OpenViduSession openViduSession) {
        opsValueOperation.set(sessionName, openViduSession);
    }

    public void delete(String sesionName) {
        opsValueOperation.set(sesionName, "", 1, TimeUnit.MILLISECONDS);
    }

}
