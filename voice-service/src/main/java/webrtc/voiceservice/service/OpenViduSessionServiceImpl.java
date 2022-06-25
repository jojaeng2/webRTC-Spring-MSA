package webrtc.voiceservice.service;

import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.voiceservice.controller.HttpApiController;
import webrtc.voiceservice.domain.OpenViduSession;
import webrtc.voiceservice.domain.User;
import webrtc.voiceservice.dto.SessionDto;
import webrtc.voiceservice.dto.SessionDto.GetTokenRequest;
import webrtc.voiceservice.dto.SessionDto.RemoveUserInSessionRequest;
import webrtc.voiceservice.repository.OpenViduSessionRepository;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenViduSessionServiceImpl implements OpenViduSessionService {

    @Value("${openvidu.url}")
    private String openViduUrl;

    @Value("${openvidu.secret}")
    private String secret;

    private OpenVidu openVidu;

    @PostConstruct
    public OpenVidu openVidu() {
        return openVidu = new OpenVidu(openViduUrl, secret);
    }

    private final HttpApiController httpApiController;
    private final OpenViduSessionRepository openViduSessionRepository;


    @Transactional
    public String createToken(GetTokenRequest request, User user) {
        String sessionName = request.getSessionName();

        // Role associated to this user
        OpenViduRole role = OpenViduRole.PUBLISHER;

        String serverData = "{\"serverData\": " +
                "\"" +
                    user.getNickname() +
                "\"}";

        // Create Connection Properties
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder()
                .type(ConnectionType.WEBRTC)
                .data(serverData)
                .role(role)
                .build();

        OpenViduSession openViduSession = findOpenViduSessionByName(sessionName);

        // session이 이미 존재하는 경우
        if(openViduSession != null) {
            try {
                // session 객체와 연결 후 속성 설정
                List<Session> sessions = this.openVidu.getActiveSessions();
                String token;
                for (Session session : sessions) {
                    if(session.getSessionId().equals(openViduSession.getSessionId())) {
                        // response로 token 반환
                        return session.createConnection(connectionProperties).getToken();
                    }
                }

            } catch(OpenViduJavaClientException e) {

            } catch(OpenViduHttpException e) {
                if(e.getStatus() == 404) {
                    // Invalid sessionId, Clean Collections and continue as new session
                }
            }
        }
        else {
            // session 새롭게 생성
            try {
                Session session = this.openVidu.createSession();
                // Generate a new Connection With the recently created connectionProperties
                String token = session.createConnection(connectionProperties).getToken();

                // Store the session and the token
                createOpenViduSession(sessionName, user, token, session.getSessionId());
                return token;
            } catch(Exception e) {
                // If Error generate an error message and return it to client

            }
        }
    }

    @Transactional
    public void removeUserInOpenViduSession(RemoveUserInSessionRequest request, User user) {
        String token = request.getToken();
        String sessionName = request.getSessionName();
        OpenViduSession openViduSession = findOpenViduSessionByName(sessionName);

        if(openViduSession != null) {
            if(openViduSession.getUsers().get(token).equals(user)) {

                openViduSession.getUsers().remove(token);
                openViduSessionRepository.update(sessionName, openViduSession);

                if(openViduSession.getUsers().isEmpty()) {
                    // 현재 나가는 유저가 마지막인 경우 -> Session delete
                    deleteOpenViduSession(sessionName);
                }
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

    public OpenViduSession findOpenViduSessionByName(String sessionName) {
        return openViduSessionRepository.findOpenViduSessionByName(sessionName);
    }

    public OpenViduSession createOpenViduSession(String sessionName, User user, String token, String sessionId) {
        OpenViduSession openViduSession = new OpenViduSession(sessionName, user, token, sessionId);
        openViduSessionRepository.createSession(sessionName, openViduSession);
        return openViduSession;
    }

    public void deleteOpenViduSession(String sessionName) {
        openViduSessionRepository.delete(sessionName);
    }
}
