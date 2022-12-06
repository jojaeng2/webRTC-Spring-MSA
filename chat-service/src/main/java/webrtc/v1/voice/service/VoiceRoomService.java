package webrtc.v1.voice.service;


import webrtc.v1.user.entity.Users;
import webrtc.v1.voice.dto.VoiceRoomDto.GetTokenRequest;
import webrtc.v1.voice.dto.VoiceRoomDto.RemoveUserInSessionRequest;

public interface VoiceRoomService {

    String getToken(GetTokenRequest request, Users user);

    void removeUserInVoiceRoom(RemoveUserInSessionRequest request, String email);

}
