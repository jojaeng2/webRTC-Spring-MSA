package webrtc.voiceservice.controller.session;


import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webrtc.voiceservice.controller.HttpApiController;
import webrtc.voiceservice.domain.User;
import webrtc.voiceservice.dto.SessionDto.GetTokenRequest;
import webrtc.voiceservice.dto.SessionDto.GetTokenResponse;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api-sessions")
@RequiredArgsConstructor
public class SessionController {


    @Value("{openvidu.url}")
    private String openViduUrl;

    @Value("${openvidu.secret}")
    private String secret;

    private OpenVidu openVidu = new OpenVidu(openViduUrl, secret);

    private final HttpApiController httpApiController;

    // 임시로 Memory로 Session 관리
    private Map<String, Session> sessionMap = new HashMap<>();

    // sessions name과 token으로 OpenViduRole 저장을 위한 2차원 Map
    private Map<String, Map<String, OpenViduRole>> mapSessionNamesTokens = new HashMap<>();

    @PostMapping("/get-token")
    public ResponseEntity getToken(@RequestBody GetTokenRequest request, HttpSession httpSession) throws ParseException {

        String email = request.getEmail();
        User user = httpApiController.postFindUserByEmail(email);

        // The video-call to connect
        String sessionName = request.getSessionName();

        // Role associated to this user
        OpenViduRole role = OpenViduRole.PUBLISHER;

        String serverData = "{\"serverData\": \"" + user.toString() + "\"}";

        // Create Connection Properties
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder()
                .type(ConnectionType.WEBRTC)
                .data(serverData)
                .role(role)
                .build();

        if(this.sessionMap.get(sessionName) != null) {
            // session이 이미 존재하는 경우
            System.out.println("Session is already exist");

            try {
                // session 객체와 연결 후 속성 설정
                String token = this.sessionMap.get(sessionName).createConnection(connectionProperties).getToken();

                // Token Collection update
                this.mapSessionNamesTokens.get(sessionName).put(token, role);

                // response로 token 반환
                return new ResponseEntity(new GetTokenResponse(token), HttpStatus.OK);

            } catch(OpenViduJavaClientException e) {
                return getErrorResponse(e);
            } catch(OpenViduHttpException e) {
                if(e.getStatus() == 404) {
                    // Invalid sessionId, Clean Collections and continue as new session
                    this.sessionMap.remove(sessionName);
                    this.mapSessionNamesTokens.remove(sessionName);
                }
            }
        }
        else {
            // session 새롭게 생성
            System.out.println("New Session = " + sessionName);
            try {

                // Create a new OpenVidu Session
                Session session = this.openVidu.createSession();

                // Generate a new Connection With the recently created connectionProperties
                String token = session.createConnection(connectionProperties).getToken();

                // Store the session and the token in our collections
                this.sessionMap.put(sessionName, session);
                this.mapSessionNamesTokens.put(sessionName, new HashMap<>());
                this.mapSessionNamesTokens.get(sessionName).put(token, role);

                // Return the response to the client
                return new ResponseEntity(new GetTokenResponse(token), HttpStatus.OK);
            } catch(Exception e) {
                // If Error generate an error message and return it to client
                return getErrorResponse(e);
            }
        }

        return new ResponseEntity(new GetTokenResponse("token"), HttpStatus.OK);
    }

    @PostMapping("/remove-user")
    public ResponseEntity removeUser(@RequestBody String sessionNameToken, HttpSession httpSession) throws Exception{

        System.out.println("Removing user | {sessionName, token} = " + sessionNameToken);

        // RequestBody -> JSON
        JSONObject sessionNameTokenJSON = (JSONObject) new JSONParser().parse(sessionNameToken);
        String sessionName = (String) sessionNameTokenJSON.get("sessionName");
        String token = (String) sessionNameTokenJSON.get("token");

        if(this.sessionMap.get(sessionName) != null && this.mapSessionNamesTokens.get(sessionName) != null) {
            // If the session exists

            if(this.mapSessionNamesTokens.get(sessionName).remove(token) != null) {
                // If the token exists
                if(this.mapSessionNamesTokens.get(sessionName).isEmpty()) {
                    // 현재 나가는 유저가 마지막인 경우 -> Session delete
                    this.sessionMap.remove(sessionName);
                }
                return new ResponseEntity(HttpStatus.OK);
            } else {
                // token invalid
                System.out.println("Problems in the app server");
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            // Session does not exist
            System.out.println("Problems in the app server : the SESSION does not exist");
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<JSONObject> getErrorResponse(Exception e) {
        JSONObject json = new JSONObject();
        json.put("cause", e.getCause());
        json.put("error", e.getMessage());
        json.put("exception", e.getClass());
        return new ResponseEntity<>(json, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
