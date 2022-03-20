package webrtc.openvidu.domain.channel.dto;

import lombok.Getter;

@Getter
public class CreateChannelResponse {
    private String channelName;
    private Long limitParticipants;

    public CreateChannelResponse(String channelName, Long limitParticipants) {
        this.channelName = channelName;
        this.limitParticipants = limitParticipants;
    }
}
