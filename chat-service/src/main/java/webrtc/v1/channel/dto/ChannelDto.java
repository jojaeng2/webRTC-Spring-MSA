package webrtc.v1.channel.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.entity.ChannelHashTag;
import webrtc.v1.channel.entity.ChannelUser;
import webrtc.v1.enums.ChannelType;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ChannelDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateChannelRequest {
        private String channelName;
        private List<String> hashTags;
        private ChannelType channelType;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtensionChannelTTLRequest {
        private long requestTTL;
    }

    @Getter
    public static class CreateChannelResponse {

        private final String channelName;
        private final long limitParticipants;
        private final long currentParticipants;
        private final long timeToLive;

        @JsonIgnore
        private final ChannelType channelType;
        @JsonIgnore
        private final List<ChannelUser> channelUsers;
        @JsonIgnore
        private final String id;



        public CreateChannelResponse(Channel channel) {
            this.id = channel.getId();
            this.channelUsers = channel.getChannelUsers();
            this.channelName = channel.getChannelName();
            this.limitParticipants = channel.getLimitParticipants();
            this.currentParticipants = channel.getCurrentParticipants();
            this.timeToLive = channel.getTimeToLive();
            this.channelType = channel.getChannelType();
        }
    }


    @Getter
    @NoArgsConstructor
    public static class FindAllChannelResponse {
        private List<ChannelResponse> channels;

        public FindAllChannelResponse(List<Channel> channels) {
            this.channels = channels.stream()
                    .map(ChannelResponse::new)
                    .collect(toList());
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ChannelResponse {
        private String id;
        private String channelName;
        private long limitParticipants;
        private long currentParticipants;
        private long timeToLive;
        private List<ChannelHashTag> channelHashTags;
        private ChannelType channelType;

        public ChannelResponse(Channel channel) {
            this.id = channel.getId();
            this.channelName = channel.getChannelName();
            this.limitParticipants = channel.getLimitParticipants();
            this.currentParticipants = channel.getCurrentParticipants();
            this.timeToLive = channel.getTimeToLive();
            this.channelHashTags = channel.getChannelHashTags();
            this.channelType = channel.getChannelType();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ExtensionChannelTTLResponse {
        private long channelTTL;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChannelTTLWithUserPointResponse {
        private long channelTTL;
        private int point;
    }

}

