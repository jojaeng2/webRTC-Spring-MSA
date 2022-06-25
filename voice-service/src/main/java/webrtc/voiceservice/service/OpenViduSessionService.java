package webrtc.voiceservice.service;

import webrtc.voiceservice.domain.OpenViduSession;
import webrtc.voiceservice.domain.User;
import webrtc.voiceservice.dto.SessionDto;

public interface OpenViduSessionService {

    String createToken(SessionDto.GetTokenRequest request, User user);

    OpenViduSession findOpenViduSessionByName(String sessionName);

    OpenViduSession createOpenViduSession(String sessionName, User user, String token, String sessionId);

    void removeUserInOpenViduSession(SessionDto.RemoveUserInSessionRequest request, User user);

    void deleteOpenViduSession(String sessionName);
}
