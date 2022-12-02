package webrtc.v1.repository.voice;


import webrtc.v1.domain.VoiceRoom;

import java.util.Optional;

public interface VoiceRoomRepository {

    void save(String sessionName, VoiceRoom voiceRoom);

    Optional<VoiceRoom> findById(String id);


    void update(String sessionName, VoiceRoom openViduSession);

    void delete(String sessionName);
}
