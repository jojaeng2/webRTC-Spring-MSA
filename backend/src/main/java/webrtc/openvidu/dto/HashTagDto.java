package webrtc.openvidu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import webrtc.openvidu.domain.Channel;

import java.util.List;

public class HashTagDto {

    @Getter
    @AllArgsConstructor
    public static class HashTagResponse {
        private List<Channel> channels;

    }

}
