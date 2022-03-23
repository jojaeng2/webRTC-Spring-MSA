package webrtc.openvidu.dto.chat;

import lombok.Getter;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.enums.SocketServerMessageType;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ServerMessage extends PublishMessage{
    private String channelId;
    private Map<Long, User> users = new HashMap<>();

    public ServerMessage(SocketServerMessageType type, String channelId, Map<Long, User> users) {
        super(type);
        this.channelId = channelId;
        this.users = users;
    }
}
