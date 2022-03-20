package webrtc.openvidu.domain.channel.dto;

import lombok.Getter;

@Getter
public class EnterChannelResponse {

    public enum ResponseType {
        ENTERFAIL, ENTERSUCCESS, SERVERERROR
    };

    private ResponseType type;
    private String message;

    public EnterChannelResponse(ResponseType type, String message) {
        this.type = type;
        this.message = message;
    }
}
