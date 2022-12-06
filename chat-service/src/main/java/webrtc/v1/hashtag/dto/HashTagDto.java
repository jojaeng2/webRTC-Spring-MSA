package webrtc.v1.hashtag.dto;

import lombok.Getter;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.dto.ChannelDto.ChannelResponse;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class HashTagDto {

    @Getter
    public static class HashTagResponse {
        private final List<ChannelResponse> channels;
        public HashTagResponse(List<Channel> channels) {
            this.channels = channels.stream()
                    .map(ChannelResponse::new)
                    .collect(toList());
        }
    }
}
