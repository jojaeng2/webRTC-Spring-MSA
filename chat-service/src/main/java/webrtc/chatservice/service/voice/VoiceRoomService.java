package webrtc.chatservice.service.voice;


import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.voice.SessionDto.GetTokenRequest;
import webrtc.chatservice.dto.voice.SessionDto.RemoveUserInSessionRequest;

public interface VoiceRoomService {

    String createToken(GetTokenRequest request, Users user);

    void removeUserInVoiceRoom(RemoveUserInSessionRequest request, Users user);

}
