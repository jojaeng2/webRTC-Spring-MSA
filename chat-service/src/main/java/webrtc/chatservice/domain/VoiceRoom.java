package webrtc.chatservice.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class VoiceRoom implements Serializable {

    private static final Long serialVersionUID = -32831239123123123L;
    private String sessionName;
    private String sessionId;
    private Map<String, String> users;

    public VoiceRoom(String sessionName, Users user, String token, String sessionId) {
        this.sessionName = sessionName;
        this.sessionId = sessionId;
        this.users = new HashMap();
        this.users.put(user.getEmail(), token);
    }
}
