package webrtc.voiceservice.service.session;

import webrtc.voiceservice.domain.OpenViduSession;
import webrtc.voiceservice.domain.User;
import webrtc.voiceservice.dto.SessionDto;

public interface OpenViduSessionService {

    String createToken(SessionDto.GetTokenRequest request, User user);

    OpenViduSession findOpenViduSessionByName(String channelId);

    OpenViduSession createOpenViduSession(String channelId, User user, String token, String sessionId);

    void deletedChannel(String channelId);

    void removeUserInOpenViduSession(SessionDto.RemoveUserInSessionRequest request, User user);

    void deleteOpenViduSession(String channelId);
}
