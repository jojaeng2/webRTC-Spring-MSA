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
import java.util.List;

import static java.util.stream.Collectors.toList;

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
    public String createToken(GetTokenRequest request, Users user) {
        String name = request.getSessionName();
        OpenViduRole role = OpenViduRole.PUBLISHER;
        String data = createServerDate(user.getEmail());

        ConnectionProperties connectionProperties = createConnectionProperties(data, role);

        VoiceRoom voiceRoom = voiceRoomRepository.findOpenViduSessionByName(name)
                .orElseThrow(AlreadyRemovedSessionInOpenViduServer::new);

        // session이 이미 존재하는 경우
        try {
            // session 객체와 연결 후 속성 설정
            List<Session> sessions = openVidu.getActiveSessions();
            sessions.stream()
                    .filter(session -> isSessionEqualToVoiceRoom(session, voiceRoom))
                    .collect(toList());
            if (!sessions.isEmpty()) {
                String token = sessions.stream()
                        .findFirst()
                        .orElseThrow().createConnection(connectionProperties).getToken();
                voiceRoom.addUser(user, token);
                voiceRoomRepository.update(name, voiceRoom);
                return token;
            }

        } catch (OpenViduJavaClientException e) {
        } catch (OpenViduHttpException e) {
            throw new OpenViduClientException();
        }


        // session 새롭게 생성
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

            Session session = this.openVidu.createSession(sessionProperties);

            // Generate a new Connection With the recently created connectionProperties
            String token = session.createConnection(connectionProperties).getToken();

            // Store the session and the token
            createVoiceRoom(request.getSessionName(), user, token, session.getSessionId());
            return token;
        } catch (Exception e) {
            // If Error generate an error message and return it to client
            throw new OpenViduClientException();
        }
    }

    @Transactional
    public void removeUserInVoiceRoom(RemoveUserInSessionRequest request, Users user) {
        String name = request.getSessionName();
        String email = request.getEmail();
        VoiceRoom voiceRoom = voiceRoomRepository.findOpenViduSessionByName(name)
                .orElseThrow(AlreadyRemovedSessionInOpenViduServer::new);
        if (voiceRoom.isValidUserToken(email, request.getToken())) {
            voiceRoom.removeUserToken(email);
            voiceRoomRepository.update(name, voiceRoom);
        }
        if (voiceRoom.isEmpty()) {
            voiceRoomRepository.delete(name);
        }
    }

    boolean isSessionEqualToVoiceRoom(Session session, VoiceRoom voiceRoom) {
        return session.getSessionId().equals(voiceRoom.getSessionId());
    }

    void createVoiceRoom(String channelId, Users user, String token, String sessionId) {
        VoiceRoom voiceRoom = VoiceRoom.builder()
                .sessionName(channelId)
                .sessionId(sessionId)
                .build();
        voiceRoom.addUser(user, token);
        voiceRoomRepository.save(channelId, voiceRoom);
    }

    String createServerDate(String email) {
        return "{\"serverData\": " +
                "\"" +
                email +
                "\"}";
    }

    ConnectionProperties createConnectionProperties(String data, OpenViduRole role) {
        return new ConnectionProperties.Builder()
                .type(ConnectionType.WEBRTC)
                .data(data)
                .role(role)
                .build();
    }
}