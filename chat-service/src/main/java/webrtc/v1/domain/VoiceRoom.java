package webrtc.v1.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.v1.exception.VoiceException.InvalidAccessToOpenViduServerException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class VoiceRoom implements Serializable {

    private static final Long serialVersionUID = -32831239123123123L;
    private String sessionName;
    private String sessionId;

    @Builder.Default
    private Map<String, String> users = new HashMap<>();

    public void addUser(Users user, String token) {
        this.users.put(user.getEmail(), token);
    }

    public boolean isValidToken(String email, String token) {
        if(Objects.equals(users.get(email), token)) {
            return true;
        }
        throw new InvalidAccessToOpenViduServerException()              ;
    }

    public void removeToken(String email) {
        users.remove(email);
    }

    public boolean isEmpty() {
        return users.isEmpty();
    }
}
