package webrtc.v1.voice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.v1.user.entity.Users;
import webrtc.v1.voice.exception.VoiceException.InvalidAccessToOpenViduServerException;

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
    private String name;
    private String id;

    @Builder.Default
    private Map<String, String> users = new HashMap<>();

    public void addUser(Users user, String token) {
        this.users.put(user.getEmail(), token);
    }

    public boolean isValidToken(String userId, String token) {
        if(Objects.equals(users.get(userId), token)) {
            return true;
        }
        throw new InvalidAccessToOpenViduServerException()              ;
    }

    public void removeToken(String userId) {
        users.remove(userId);
    }

    public boolean isEmpty() {
        return users.isEmpty();
    }
}
