package webrtc.openvidu.dto.channel;

import lombok.Getter;
import webrtc.openvidu.domain.channel.Channel;

@Getter
public class EnterChannelResponse {

    public enum ResponseType {
        ENTERFAIL, ENTERSUCCESS, SERVERERROR
    };

    private ResponseType type;
    private String message;
    private Channel channel;

    public EnterChannelResponse(ResponseType type, String message, Channel channel) {
        this.type = type;
        this.message = message;
        this.channel = channel;
    }
}
