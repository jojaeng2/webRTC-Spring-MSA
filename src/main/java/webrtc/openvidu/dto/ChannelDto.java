package webrtc.openvidu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.domain.ChannelHashTag;
import webrtc.openvidu.domain.HashTag;

import java.util.List;
import java.util.Set;

public class ChannelDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateChannelRequest {
        private String channelName;
        private List<String> hashTags;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtensionChannelTTLRequest {
        private Long requestTTL;
    }

    @Getter
    public static class CreateChannelResponse {

        private String channelName;
        private Long limitParticipants;
        private Long currentParticipants;
        private Long timeToLive;

        public CreateChannelResponse(String channelName, Long limitParticipants, Long currentParticipants, Long timeToLive) {
            this.channelName = channelName;
            this.limitParticipants = limitParticipants;
            this.currentParticipants = currentParticipants;
            this.timeToLive = timeToLive;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class FindOneChannelResponse {
        private String channelId;
        private String channelName;
        private Long limitParticipants;
        private Long currentParticipants;
        private Long timeToLive;

    }

    @Getter
    @AllArgsConstructor
    public static class FindAllChannelResponse {
        private List<ChannelResponse> channels;
    }

    @Getter
    @AllArgsConstructor
    public static class ChannelResponse {
        private String id;
        private String channelName;
        private Long limitParticipants;
        private Long currentParticipants;
        private Long timeToLive;
        private Set<ChannelHashTag> channelHashTags;
    }

    @Getter
    public static class EnterChannelResponse {

        private String code;
        private String message;

        public EnterChannelResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ExtensionChannelTTLResponse {
        private Long channelTTL;
    }

}

