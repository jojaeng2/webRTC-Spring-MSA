package webrtc.voiceservice.repository.session;

import webrtc.voiceservice.domain.OpenViduSession;

public interface OpenViduSessionRepository {

    void createSession(String sessionName, OpenViduSession openViduSession);

    OpenViduSession findOpenViduSessionByName(String sessionId);

    void deletedChannel(String channelId);

    void update(String sessionName, OpenViduSession openViduSession);

    void delete(String sessionName);
}
