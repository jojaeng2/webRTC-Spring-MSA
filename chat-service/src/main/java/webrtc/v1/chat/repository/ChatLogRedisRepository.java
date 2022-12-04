package webrtc.v1.chat.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Repository
public class ChatLogRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> opsValueOperation;
    private final String chatLog = "chatLog ";

    @PostConstruct
    private void init() {
        opsValueOperation = redisTemplate.opsForValue();
    }

    public Integer findLastIndex(String id) {
        Integer index = (Integer) opsValueOperation.get(chatLog + id);
        if(index == null) {
            index = 1;
        }
        return index;
    }

    public void addLastIndex(String id) {
        Integer index = (Integer) opsValueOperation.get(chatLog + id);
        if(index == null) {
            index = 1;
        }
        opsValueOperation.set(chatLog + id, index+1);

    }

    public void delete(String id) {
        redisTemplate.delete(id);
    }
}
