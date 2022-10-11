package webrtc.chatservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.dto.ChannelDto.ChannelResponse;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class HashTagDto {

    @Getter
    public static class HashTagResponse {
        private final List<ChannelResponse> channels;

        public HashTagResponse(List<Channel> channels) {
            System.out.println("channels.size() = " + channels.size());
            this.channels = channels.stream()
                    .map(ChannelResponse::new)
                    .collect(toList());;
        }
    }

}
