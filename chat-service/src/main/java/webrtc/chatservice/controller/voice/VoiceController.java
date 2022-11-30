package webrtc.chatservice.controller.voice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.voice.SessionDto;
import webrtc.chatservice.dto.voice.SessionDto.GetTokenRequest;
import webrtc.chatservice.dto.voice.SessionDto.GetTokenResponse;
import webrtc.chatservice.service.user.UsersService;
import webrtc.chatservice.service.voice.VoiceRoomService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/webrtc/voice")
@RequiredArgsConstructor
public class VoiceController {

    private final VoiceRoomService voiceRoomService;
    private final UsersService userService;

    @PostMapping("/get-token")
    public ResponseEntity<?> getToken(@RequestBody GetTokenRequest request) {
        Users user = userService.findOneByEmail(request.getEmail());
        String token = voiceRoomService.createToken(request, user);
        return new ResponseEntity(new GetTokenResponse(token), HttpStatus.OK);
    }

    @PostMapping("/remove-user")
    public ResponseEntity<?> removeUser(@RequestBody SessionDto.RemoveUserInSessionRequest request) throws Exception {
        Users user = userService.findOneByEmail(request.getEmail());
        voiceRoomService.removeUserInVoiceRoom(request, user);
        return new ResponseEntity(HttpStatus.OK);
    }
}
