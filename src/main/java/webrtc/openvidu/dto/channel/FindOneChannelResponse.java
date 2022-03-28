package webrtc.openvidu.dto.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class FindOneChannelResponse {
    private String channelId;
    private String channelName;
    private Long limitParticipants;
    private Long currentParticipants;
    private Long timeToLive;

}
