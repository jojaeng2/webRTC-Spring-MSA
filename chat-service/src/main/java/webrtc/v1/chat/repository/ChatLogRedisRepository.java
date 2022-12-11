package webrtc.v1.chat.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import webrtc.v1.chat.entity.ChatLog;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static webrtc.v1.chat.enums.ChatLogCount.LOADING;

@RequiredArgsConstructor
@Repository
@Slf4j
public class ChatLogRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private ValueOperations<String, Object> opsValueOperation;
    private final String chatLog = "chatLog - ";
    private final long ttl = 60L * 60L;

    @PostConstruct
    private void init() {
        opsValueOperation = redisTemplate.opsForValue();
    }

    public void save(String channelId, ChatLog chatLog) {
        opsValueOperation.set(channelId + " - " + chatLog.getIdx(), chatLog);
        redisTemplate.expire(channelId + " - " + chatLog.getIdx(), ttl, TimeUnit.SECONDS);
    }

    public List<ChatLog> findByChannelIdAndIndex(String channelId, Integer index) {
        List<ChatLog> chatLogs = new ArrayList<>();
        for (int i=Math.max(0, index - (LOADING.getCount())); i<=index-1; i++) {
            System.out.println("index = " + i);
            ChatLog chatLog = objectMapper.convertValue(opsValueOperation.get(channelId + " - " + i), ChatLog.class);
            if (chatLog == null) {
                log.info("chatLog is Null!!");
                continue;
            }
            log.info(chatLog.getId());
            chatLogs.add(chatLog);
        }
        return chatLogs;
    }


    public Integer findLastIndex(String id) {
        Integer index = (Integer) opsValueOperation.get(chatLog + id);
        if(index == null) {
            index = 0;
        }
        return index;
    }

    public void addLastIndex(String id) {
        Integer index = (Integer) opsValueOperation.get(chatLog + id);
        if(index == null) {
            index = 0;
        }
        opsValueOperation.set(chatLog + id, index+1);
    }

    public void delete(String id) {
        redisTemplate.delete(chatLog + id);
    }
}
