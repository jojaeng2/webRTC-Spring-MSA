package webrtc.openvidu.controller;

import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.Session;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webrtc.openvidu.controller.requestDto.GetTokenRequest;
import webrtc.openvidu.controller.responseDto.GetTokenResponse;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api-sessions")
public class SessionController {

    private OpenVidu openVidu;
    private String openViduUrl;
    private String Secret;

    private Map<String, Session> sessionMap = new HashMap<>();

    public SessionController(@Value("${openvidu.secret}") String secret, @Value("{openvidu.url}") String openViduUrl) {
        this.Secret = secret;
        this.openViduUrl = openViduUrl;
        this.openVidu = new OpenVidu(openViduUrl, Secret);
    }

    @PostMapping("/get-token")
    public ResponseEntity getToken(@RequestBody GetTokenRequest request) {

        String sessionName = request.getSessionName();

        return new ResponseEntity(new GetTokenResponse(), HttpStatus.OK);
    }


}
