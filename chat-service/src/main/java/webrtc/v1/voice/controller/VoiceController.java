package webrtc.v1.voice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.service.UsersService;
import webrtc.v1.utils.jwt.JwtTokenUtil;
import webrtc.v1.voice.dto.VoiceRoomDto.GetTokenRequest;
import webrtc.v1.voice.dto.VoiceRoomDto.GetTokenResponse;
import webrtc.v1.voice.dto.VoiceRoomDto.RemoveUserInSessionRequest;
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
    String userId = jwtTokenUtil.getUserIdFromToken(jwtAccessToken.substring(4));
    Users user = userService.findOneById(userId);
    String token = voiceRoomService.getToken(request, user);
    return new ResponseEntity(new GetTokenResponse(token), HttpStatus.OK);
  }

  // jwt 토큰 적용해야함
  @PostMapping("/remove-user")
  public ResponseEntity<?> removeUser(
      @RequestHeader("Authorization") String jwtAccessToken,
      @RequestBody RemoveUserInSessionRequest request
  ) {
    String userId = jwtTokenUtil.getUserIdFromToken(jwtAccessToken.substring(4));
    voiceRoomService.removeUserInVoiceRoom(request, userId);
    return new ResponseEntity(HttpStatus.OK);
  }
}
