package webrtc.openvidu.dto.chat;

import lombok.Getter;
import webrtc.openvidu.enums.SocketServerMessageType;

@Getter
public class PublishMessage {
    private SocketServerMessageType type;

    public PublishMessage(SocketServerMessageType type) {
        this.type = type;
    }
}
