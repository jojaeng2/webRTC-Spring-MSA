package webrtc.v1.service.voice;


import webrtc.v1.domain.Users;
import webrtc.v1.dto.voice.SessionDto.GetTokenRequest;
import webrtc.v1.dto.voice.SessionDto.RemoveUserInSessionRequest;

public interface VoiceRoomService {

    String createToken(GetTokenRequest request, Users user);

    void removeUserInVoiceRoom(RemoveUserInSessionRequest request, Users user);

}
