package webrtc.chatservice.repository.voice;


import webrtc.chatservice.domain.VoiceRoom;

public interface OpenViduSessionRepository {

    void createSession(String sessionName, VoiceRoom voiceRoom);

    VoiceRoom findOpenViduSessionByName(String sessionId);

    void deletedChannel(String channelId);

    void update(String sessionName, VoiceRoom openViduSession);

    void delete(String sessionName);
}
