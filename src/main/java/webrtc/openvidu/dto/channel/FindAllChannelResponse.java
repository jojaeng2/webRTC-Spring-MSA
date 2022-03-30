package webrtc.openvidu.dto.channel;

import lombok.Getter;
import webrtc.openvidu.domain.Channel;

import java.util.List;

@Getter
public class FindAllChannelResponse {

    private List<Channel> channels;

    public FindAllChannelResponse(List<Channel> channels) {
        this.channels = channels;
    }
}
