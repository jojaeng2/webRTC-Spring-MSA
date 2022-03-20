package webrtc.openvidu.domain.channel.dto;

import lombok.Getter;
import webrtc.openvidu.domain.channel.Channel;

import java.util.List;

@Getter
public class FindAllChannelResponse {

    private List<Channel> channels;

    public FindAllChannelResponse(List<Channel> channels) {
        this.channels = channels;
    }
}
