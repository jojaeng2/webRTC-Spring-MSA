package webrtc.openvidu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.enums.HttpReturnType;

import java.util.List;

public class ChannelDto {

    @Getter
    public static class CreateChannelRequest {
        private String channelName;
        private Long limitParticipants;
        private List<String> hashTags;
    }

    @Getter
    public static class CreateChannelResponse {

        private HttpReturnType type;
        private String channelName;
        private Long limitParticipants;
        private Long currentParticipants;
        private Long timeToLive;

        public CreateChannelResponse(HttpReturnType type, String channelName, Long limitParticipants, Long currentParticipants, Long timeToLive) {
            this.type = type;
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
    public static class FindAllChannelResponse {

        private List<Channel> channels;

        public FindAllChannelResponse(List<Channel> channels) {
            this.channels = channels;
        }
    }

    @Getter
    public static class EnterChannelRequest {
        private Long userId;
    }

    @Getter
    public static class EnterChannelResponse {

        private HttpReturnType type;
        private String message;

        public EnterChannelResponse(HttpReturnType type, String message) {
            this.type = type;
            this.message = message;
        }
    }

}
