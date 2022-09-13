package webrtc.voiceservice.controller.session;


import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.voiceservice.controller.HttpApiController;
import webrtc.voiceservice.domain.User;
import webrtc.voiceservice.dto.SessionDto.GetTokenRequest;
import webrtc.voiceservice.dto.SessionDto.GetTokenResponse;
import webrtc.voiceservice.dto.SessionDto.RemoveUserInSessionRequest;
import webrtc.voiceservice.service.session.OpenViduSessionService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/webrtc/voice")
@RequiredArgsConstructor
public class SessionController {

    private final OpenViduSessionService openViduSessionService;
    private final HttpApiController httpApiController;
    @PostMapping("/get-token")
    public ResponseEntity<?> getToken(@RequestBody GetTokenRequest request) {
        User user = httpApiController.postFindUserByEmail(request.getEmail());
        String token = openViduSessionService.createToken(request, user);
        return new ResponseEntity(new GetTokenResponse(token), HttpStatus.OK);
    }

    @PostMapping("/remove-user")
    public ResponseEntity<?> removeUser(@RequestBody RemoveUserInSessionRequest request) throws Exception{
        String email = request.getEmail();
        User user = httpApiController.postFindUserByEmail(email);
        openViduSessionService.removeUserInOpenViduSession(request, user);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/remove-channel")
    public ResponseEntity<?> removeChannel(@RequestBody String channelId) {
        openViduSessionService.deletedChannel(channelId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
