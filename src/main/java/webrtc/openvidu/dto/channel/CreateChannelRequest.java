package webrtc.openvidu.dto.channel;

import lombok.Getter;

import java.util.List;

@Getter
public class CreateChannelRequest {
    private String channelName;
    private Long limitParticipants;
    private List<String> hashTags;
}
