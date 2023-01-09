package webrtc.v1.chat.repository;

import static webrtc.v1.chat.enums.RedisKeys.BLANK;
import static webrtc.v1.chat.enums.RedisKeys.CHAT_LOG;
import static webrtc.v1.chat.enums.RedisKeys.LAST_INDEX;
import static webrtc.v1.point.enums.PointUnit.CREATE_CHANNEL;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import webrtc.v1.chat.entity.ChatLog;

@RequiredArgsConstructor
@Repository
@Slf4j
public class ChatLogRedisRepositoryImpl {

  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;
  private ValueOperations<String, Object> opsValueOperation;

  @PostConstruct
  private void init() {
    opsValueOperation = redisTemplate.opsForValue();
  }

  public void save(String channelId, ChatLog chatLog) {
    opsValueOperation.set(CHAT_LOG.getPrefix() + channelId + BLANK.getPrefix() + chatLog.getIdx(),
        chatLog);
    redisTemplate.expire(CHAT_LOG.getPrefix() + channelId + BLANK.getPrefix() + chatLog.getIdx(),
        CREATE_CHANNEL.getUnit(), TimeUnit.SECONDS);
  }

  public Optional<ChatLog> findByChannelIdAndIndex(String channelId, Integer index) {
    return Optional.of(objectMapper.convertValue(
        opsValueOperation.get(CHAT_LOG.getPrefix() + channelId + BLANK.getPrefix() + index),
        ChatLog.class));
  }


  public Optional<Integer> findLastIndex(String id) {
    return Optional.of(objectMapper.convertValue(opsValueOperation.get(LAST_INDEX.getPrefix() + id),
        Integer.class));
  }

  public void addLastIndex(String id) {
    Integer index = findLastIndex(id)
        .orElse(0);
    opsValueOperation.set(LAST_INDEX.getPrefix() + id, index + 1);
  }

  public void delete(String id) {
    redisTemplate.delete(LAST_INDEX.getPrefix() + id);
  }
}
