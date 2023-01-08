package webrtc.v1.voice.repository;


import java.util.Optional;
import webrtc.v1.voice.entity.VoiceRoom;

public interface VoiceRoomRepository {

  void save(String id, VoiceRoom voiceRoom);

  Optional<VoiceRoom> findById(String id);


  void update(String id, VoiceRoom openViduSession);

  void delete(String id);
}
