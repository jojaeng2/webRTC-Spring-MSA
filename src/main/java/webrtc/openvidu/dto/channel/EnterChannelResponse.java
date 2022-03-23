package webrtc.openvidu.dto.channel;

import lombok.Getter;
import webrtc.openvidu.enums.HttpReturnType;

@Getter
public class EnterChannelResponse {

    private HttpReturnType type;
    private String message;

    public EnterChannelResponse(HttpReturnType type, String message) {
        this.type = type;
        this.message = message;
    }
}
