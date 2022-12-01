package webrtc.v1.service.voice;

import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.domain.Users;
import webrtc.v1.domain.VoiceRoom;
import webrtc.v1.dto.voice.SessionDto.GetTokenRequest;
import webrtc.v1.dto.voice.SessionDto.RemoveUserInSessionRequest;
import webrtc.v1.exception.VoiceException.AlreadyRemovedSessionInOpenViduServer;
import webrtc.v1.exception.VoiceException.OpenViduClientException;
import webrtc.v1.repository.voice.VoiceRoomRepository;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VoiceRoomServiceImpl implements VoiceRoomService {

    @Value("${openvidu.url}")
    private String openViduUrl;

    @Value("${openvidu.secret}")
    private String secret;

    private OpenVidu openVidu;

    @PostConstruct
    public OpenVidu openVidu() {
        return openVidu = new OpenVidu(openViduUrl, secret);
    }

    private final VoiceRoomRepository voiceRoomRepository;


    @Transactional
    public String getToken(GetTokenRequest request, Users user) {
        String name = request.getSessionName();
        OpenViduRole role = OpenViduRole.PUBLISHER;
        String data = createServerDate(user.getEmail());

        ConnectionProperties connectionProperties = createConnectionProperties(data, role);
        VoiceRoom voiceRoom = voiceRoomRepository.findById(name)
                .orElseThrow(AlreadyRemovedSessionInOpenViduServer::new);

        String token = getExistToken(connectionProperties, voiceRoom);
        if(!Objects.equals(token, "")) {
            voiceRoom.addUser(user, token);
            voiceRoomRepository.update(name, voiceRoom);
            return token;
        }

        Session session = createSession();
        String token1 = createToken(connectionProperties, session);
        createVoiceRoom(request.getSessionName(), user, token1, session.getSessionId());
        return token1;
    }

    @Transactional
    public void removeUserInVoiceRoom(RemoveUserInSessionRequest request, Users user) {
        String name = request.getSessionName();
        String email = request.getEmail();
        VoiceRoom voiceRoom = voiceRoomRepository.findById(name)
                .orElseThrow(AlreadyRemovedSessionInOpenViduServer::new);
        if (voiceRoom.isValidUserToken(email, request.getToken())) {
            voiceRoom.removeUserToken(email);
            voiceRoomRepository.update(name, voiceRoom);
        }
        if (voiceRoom.isEmpty()) {
            voiceRoomRepository.delete(name);
        }
    }

    private boolean isSessionEqualToVoiceRoom(Session session, VoiceRoom voiceRoom) {
        return session.getSessionId().equals(voiceRoom.getSessionId());
    }

    private void createVoiceRoom(String channelId, Users user, String token, String sessionId) {
        VoiceRoom voiceRoom = VoiceRoom.builder()
                .sessionName(channelId)
                .sessionId(sessionId)
                .build();
        voiceRoom.addUser(user, token);
        voiceRoomRepository.save(channelId, voiceRoom);
    }

    private String createServerDate(String email) {
        return "{\"serverData\": " +
                "\"" +
                email +
                "\"}";
    }

    private ConnectionProperties createConnectionProperties(String data, OpenViduRole role) {
        return new ConnectionProperties.Builder()
                .type(ConnectionType.WEBRTC)
                .data(data)
                .role(role)
                .build();
    }

    private String getExistToken(ConnectionProperties connectionProperties, VoiceRoom voiceRoom) {
        try {
            // session 객체와 연결 후 속성 설정
            return openVidu.getActiveSessions().stream()
                    .filter(session -> isSessionEqualToVoiceRoom(session, voiceRoom))
                    .findFirst()
                    .orElseThrow()
                    .createConnection(connectionProperties)
                    .getToken();
        } catch (OpenViduJavaClientException e) {
        } catch (OpenViduHttpException e) {
            throw new OpenViduClientException();
        }
        return "";
    }

    private String createToken(ConnectionProperties connectionProperties, Session session) {
        try {
            return session.createConnection(connectionProperties).getToken();
        } catch (Exception e) {
            throw new OpenViduClientException();
        }
    }

    private Session createSession() {
        try {
            RecordingProperties recordingProperties = new RecordingProperties.Builder()
                    .outputMode(Recording.OutputMode.COMPOSED)
                    .hasAudio(true)
                    .hasVideo(false)
                    .build();
            SessionProperties sessionProperties = new SessionProperties.Builder()
                    .defaultRecordingProperties(recordingProperties)
                    .recordingMode(RecordingMode.ALWAYS)
                    .build();
            return this.openVidu.createSession(sessionProperties);
        } catch (Exception e) {
            throw new OpenViduClientException();
        }
    }
}