package webrtc.voiceservice.service.session;

import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.voiceservice.controller.HttpApiController;
import webrtc.voiceservice.domain.OpenViduSession;
import webrtc.voiceservice.domain.User;
import webrtc.voiceservice.dto.SessionDto.GetTokenRequest;
import webrtc.voiceservice.dto.SessionDto.RemoveUserInSessionRequest;
import webrtc.voiceservice.exception.SessionException.AlreadyRemovedSessionInOpenViduServer;
import webrtc.voiceservice.exception.SessionException.InvalidAccessToOpenViduServerException;
import webrtc.voiceservice.exception.SessionException.NotExistOpenViduServerException;
import webrtc.voiceservice.exception.SessionException.OpenViduClientException;
import webrtc.voiceservice.repository.session.OpenViduSessionRepository;

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
                        token = session.createConnection(connectionProperties).getToken();
                        openViduSession.getUsers().put(user.getId(), token);
                        openViduSessionRepository.update(sessionName, openViduSession);
                        return token;
                    }
                }

            } catch(OpenViduJavaClientException e) {
            } catch(OpenViduHttpException e) {
                if(e.getStatus() == 404) {
                    throw new NotExistOpenViduServerException();
                }
            }
        }


        // session 새롭게 생성
        try {
            Session session = this.openVidu.createSession();
            // Generate a new Connection With the recently created connectionProperties
            String token = session.createConnection(connectionProperties).getToken();
            System.out.println("session.getSessionId() = " + session.getSessionId());
             // Store the session and the token
            createOpenViduSession(sessionName, user, token, session.getSessionId());
            return token;
        } catch(Exception e) {
            // If Error generate an error message and return it to client
            throw new OpenViduClientException();
        }
    }

    @Transactional
    public void removeUserInOpenViduSession(RemoveUserInSessionRequest request, User user) {
        String token = request.getToken();
        String sessionName = request.getSessionName();
        OpenViduSession openViduSession = findOpenViduSessionByName(sessionName);

        if(openViduSession != null) {
            if(openViduSession.getUsers().get(user.getId()).equals(token)) {

                openViduSession.getUsers().remove(user.getId());
                openViduSessionRepository.update(sessionName, openViduSession);

                if(openViduSession.getUsers().isEmpty()) {
                    // 현재 나가는 유저가 마지막인 경우 -> Session delete
                    deleteOpenViduSession(sessionName);
                }
            } else {
                // token invalid
                throw new InvalidAccessToOpenViduServerException();
            }
        } else {
            // Session does not exist
            throw new AlreadyRemovedSessionInOpenViduServer();
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