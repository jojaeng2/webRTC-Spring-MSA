package webrtc.openvidu.dto.chat;

import lombok.Getter;

@Getter
public class ServerMessage {

    public enum MessageType {
        RENEWAL, CHATTING
    };

    private MessageType type;


}
