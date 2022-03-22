package webrtc.openvidu.dto.channel;

import lombok.Getter;

@Getter
public class CreateChannelRequest {
    private String channelName;
    private Long limitParticipants;

}
