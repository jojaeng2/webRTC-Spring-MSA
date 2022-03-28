package webrtc.openvidu.dto.channel;

import lombok.Getter;
import webrtc.openvidu.enums.HttpReturnType;

import static webrtc.openvidu.enums.HttpReturnType.SUCCESS;

@Getter
public class CreateChannelResponse {

    private HttpReturnType type;
    private String channelName;
    private Long limitParticipants;
    private Long currentParticipants;
    private Long timeToLive;

    public CreateChannelResponse(HttpReturnType type, String channelName, Long limitParticipants, Long currentParticipants, Long timeToLive) {
        this.type = type;
        this.channelName = channelName;
        this.limitParticipants = limitParticipants;
        this.currentParticipants = currentParticipants;
        this.timeToLive = timeToLive;
    }
}
