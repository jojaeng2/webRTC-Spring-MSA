package webrtc.v1.voice.service;

import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.user.entity.Users;
import webrtc.v1.voice.entity.VoiceRoom;
import webrtc.v1.voice.dto.VoiceRoomDto.GetTokenRequest;
import webrtc.v1.voice.dto.VoiceRoomDto.RemoveUserInSessionRequest;
import webrtc.v1.voice.exception.VoiceException.AlreadyRemovedSessionInOpenViduServer;
import webrtc.v1.voice.exception.VoiceException.OpenViduClientException;
import webrtc.v1.voice.repository.VoiceRoomRepository;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;

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
        String id = request.getChannelId();
        OpenViduRole role = OpenViduRole.PUBLISHER;
        String data = createServerDate(user.getEmail());

        ConnectionProperties properties = createConnectionProperties(data, role);
        Optional<VoiceRoom> voiceRoom = voiceRoomRepository.findById(id);
        if (voiceRoom.isPresent()) {
            String token = getExistToken(properties, voiceRoom.get());
            voiceRoom.get().addUser(user, token);
            voiceRoomRepository.update(id, voiceRoom.get());
            return token;
        }
        Session session = createSession();
        String newToken = createToken(properties, session);
        createVoiceRoom(request.getChannelId(), user, newToken, session.getSessionId());
        return newToken;
    }

    @Transactional
    public void removeUserInVoiceRoom(RemoveUserInSessionRequest request, String email) {
        String id = request.getChannelId();
        VoiceRoom voiceRoom = voiceRoomRepository.findById(id)
                .orElseThrow(AlreadyRemovedSessionInOpenViduServer::new);
        if (voiceRoom.isValidToken(email, request.getToken())) {
            voiceRoom.removeToken(email);
            voiceRoomRepository.update(id, voiceRoom);
        }
        if (voiceRoom.isEmpty()) {
            voiceRoomRepository.delete(id);
        }
    }

    private boolean isSessionEqualToVoiceRoom(Session session, VoiceRoom voiceRoom) {
        return session.getSessionId().equals(voiceRoom.getId());
    }

    private void createVoiceRoom(String channelId, Users user, String token, String sessionId) {
        VoiceRoom voiceRoom = VoiceRoom.builder()
                .name(channelId)
                .id(sessionId)
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