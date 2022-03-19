package webrtc.openvidu.domain.chat.dto;

import lombok.Getter;

@Getter
public class ServerMessage {

    public enum MessageType {
        RENEWAL, CHATTING
    };

    private MessageType type;


}
