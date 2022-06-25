package webrtc.voiceservice.controller.session;


import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webrtc.voiceservice.controller.HttpApiController;
import webrtc.voiceservice.domain.OpenViduSession;
import webrtc.voiceservice.domain.User;
import webrtc.voiceservice.dto.SessionDto;
import webrtc.voiceservice.dto.SessionDto.GetTokenRequest;
import webrtc.voiceservice.dto.SessionDto.GetTokenResponse;
import webrtc.voiceservice.dto.SessionDto.RemoveUserInSessionRequest;
import webrtc.voiceservice.service.OpenViduSessionService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/v1/webrtc")
@RequiredArgsConstructor
public class SessionController {

    private final OpenViduSessionService openViduSessionService;
    private final HttpApiController httpApiController;



    @PostMapping("/get-token")
    public ResponseEntity getToken(@RequestBody GetTokenRequest request) {
        User user = httpApiController.postFindUserByEmail(request.getEmail());
        String token = openViduSessionService.createToken(request, user);
        return new ResponseEntity(new GetTokenResponse(token), HttpStatus.OK);
    }

    @PostMapping("/remove-user")
    public ResponseEntity removeUser(@RequestBody RemoveUserInSessionRequest request) throws Exception{

        String email = request.getEmail();
        User user = httpApiController.postFindUserByEmail(email);
        openViduSessionService.removeUserInOpenViduSession(request, user);

    }

    private ResponseEntity<JSONObject> getErrorResponse(Exception e) {
        JSONObject json = new JSONObject();
        json.put("cause", e.getCause());
        json.put("error", e.getMessage());
        json.put("exception", e.getClass());
        return new ResponseEntity<>(json, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
