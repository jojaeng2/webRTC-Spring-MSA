package webrtc.openvidu.dto.chat;

import lombok.Getter;

@Getter
public class ClientMessage {
    private ClientMessageType type;
    private String channelId;
    private Long userId;
    private Long userName;
    private String message;
}
