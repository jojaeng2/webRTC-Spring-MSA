package webrtc.v1.voice.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
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
  private final ValueOperations<String, Object> opsValueOperation;
  private final ObjectMapper objectMapper;
  private static final String openVidu = "OpenVidu ";

  public void save(String id, VoiceRoom voiceRoom) {
    opsValueOperation.set(openVidu + id, voiceRoom);
  }

  public Optional<VoiceRoom> findById(String id) {
    VoiceRoom voiceRoom = objectMapper.convertValue(opsValueOperation.get(openVidu + id),
        VoiceRoom.class);
    return Optional.ofNullable(voiceRoom);
  }

  public void update(String id, VoiceRoom voiceRoom) {
    opsValueOperation.set(openVidu + id, voiceRoom);
  }

  public void delete(String id) {
    opsValueOperation.set(openVidu + id, "", 1, TimeUnit.MILLISECONDS);
  }

  public void deleteAll() {

  }
}
