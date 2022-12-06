package webrtc.v1.voice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.v1.user.entity.Users;
import webrtc.v1.utils.jwt.JwtTokenUtil;
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
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/get-token")
    public ResponseEntity<?> getToken(
            @RequestHeader("Authorization") String jwtAccessToken,
            @RequestBody GetTokenRequest request
    ) {
        String email = jwtTokenUtil.getUserEmailFromToken(jwtAccessToken.substring(4));
        Users user = userService.findOneByEmail(email);
        String token = voiceRoomService.getToken(request, user);
        return new ResponseEntity(new GetTokenResponse(token), HttpStatus.OK);
    }

    // jwt 토큰 적용해야함
    @PostMapping("/remove-user")
    public ResponseEntity<?> removeUser(
            @RequestHeader("Authorization") String jwtAccessToken,
            @RequestBody RemoveUserInSessionRequest request
    ) {
        String email = jwtTokenUtil.getUserEmailFromToken(jwtAccessToken.substring(4));
        Users user = userService.findOneByEmail(email);
        voiceRoomService.removeUserInVoiceRoom(request);
        return new ResponseEntity(HttpStatus.OK);
    }
}
