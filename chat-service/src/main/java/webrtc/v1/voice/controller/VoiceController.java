package webrtc.v1.voice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.v1.user.entity.Users;
import webrtc.v1.voice.dto.VoiceRoomDto.GetTokenRequest;
import webrtc.v1.voice.dto.VoiceRoomDto.GetTokenResponse;
import webrtc.v1.voice.dto.VoiceRoomDto.RemoveUserInSessionRequest;
import webrtc.v1.user.service.UsersService;
import webrtc.v1.voice.service.VoiceRoomService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/webrtc/voice")
@RequiredArgsConstructor
public class VoiceController {

    private final VoiceRoomService voiceRoomService;
    private final UsersService userService;

    @PostMapping("/get-token")
    public ResponseEntity<?> getToken(
            @RequestBody GetTokenRequest request
    ) {
        Users user = userService.findOneByEmail(request.getEmail());
        String token = voiceRoomService.getToken(request, user);
        return new ResponseEntity(new GetTokenResponse(token), HttpStatus.OK);
    }

    // jwt 토큰 적용해야함
    @PostMapping("/remove-user")
    public ResponseEntity<?> removeUser(
            @RequestBody RemoveUserInSessionRequest request
    ) {
        Users user = userService.findOneByEmail(request.getEmail());
        voiceRoomService.removeUserInVoiceRoom(request);
        return new ResponseEntity(HttpStatus.OK);
    }
}
