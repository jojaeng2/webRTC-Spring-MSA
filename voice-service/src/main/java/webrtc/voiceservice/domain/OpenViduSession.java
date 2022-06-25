package webrtc.voiceservice.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.*;

@Getter
@NoArgsConstructor
public class OpenViduSession implements Serializable {

    private static final Long serialVersionUID = -32831239123123123L;
    private String sessionName;
    private String sessionId;
    private Map<String, User> users;

    public OpenViduSession(String sessionName, User user, String token, String sessionId) {
        this.sessionName = sessionName;
        this.sessionId = sessionId;
        this.users = new HashMap();
        this.users.put(token, user);
    }


}
