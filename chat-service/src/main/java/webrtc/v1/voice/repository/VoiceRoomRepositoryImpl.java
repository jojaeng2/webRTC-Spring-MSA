package webrtc.v1.voice.repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import webrtc.v1.voice.entity.VoiceRoom;

@RequiredArgsConstructor
@Repository
public class VoiceRoomRepositoryImpl implements VoiceRoomRepository {

  private final RedisTemplate<String, Object> redisTemplate;
  private ValueOperations<String, Object> opsValueOperation;
  private static final String openVidu = "OpenVidu ";

  @PostConstruct
  private void init() {
    opsValueOperation = redisTemplate.opsForValue();
  }

  public void save(String id, VoiceRoom voiceRoom) {
    opsValueOperation.set("OpenVidu " + id, voiceRoom);
  }

  public Optional<VoiceRoom> findById(String id) {
    return Optional.ofNullable((VoiceRoom) opsValueOperation.get(openVidu + id));
  }

  public void update(String id, VoiceRoom voiceRoom) {
    opsValueOperation.set(openVidu + id, voiceRoom);
  }

  public void delete(String id) {
    opsValueOperation.set(openVidu + id, "", 1, TimeUnit.MILLISECONDS);
  }

}
