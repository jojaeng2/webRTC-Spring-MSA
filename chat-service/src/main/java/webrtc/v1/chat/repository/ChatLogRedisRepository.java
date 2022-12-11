package webrtc.v1.chat.repository;

import lombok.RequiredArgsConstructor;
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
public class ChatLogRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> opsValueOperation;
    private final String chatLog = "chatLog ";
    private final long ttl = 60L * 60L;

    @PostConstruct
    private void init() {
        opsValueOperation = redisTemplate.opsForValue();
    }

    public void save(String channelId, ChatLog chatLog) {
        opsValueOperation.set(channelId + "-" + chatLog.getIdx(), chatLog);
        redisTemplate.expire(channelId + "-" + chatLog.getIdx(), ttl, TimeUnit.SECONDS);
    }

    public List<ChatLog> findByChannelIdAndIndex(String channelId, Integer index) {
        List<ChatLog> chatLogs = new ArrayList<>();
        System.out.println("index = " + index);
        for (int i=Math.max(1, index - (LOADING.getCount())); i<=index-1; i++) {
            System.out.println("Math.max(1, index - (LOADING.getCount())); i<=index-1 = " + i);
            ChatLog chatLog = (ChatLog) opsValueOperation.get(channelId + "-" + i);
            if (chatLog == null) {
                continue;
            }
            chatLogs.add(chatLog);
        }
        return chatLogs;
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
            index = 0;
        }
        opsValueOperation.set(chatLog + id, index+1);
    }

    public void delete(String id) {
        redisTemplate.delete(chatLog + id);
    }
}
