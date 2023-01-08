package webrtc.v1.channel.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.entity.ChannelUser;
import webrtc.v1.channel.enums.ChannelType;
import webrtc.v1.hashtag.dto.HashTagDto.HashTagResponse;
import webrtc.v1.hashtag.dto.HashTagDto.TagNameResponse;

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
  public static class CreateChannelDto {

    private final String channelName;
    private final List<String> hashTags;
    private final ChannelType type;
    private final String userId;

    public CreateChannelDto(CreateChannelRequest request, String userId) {
      this.channelName = request.getChannelName();
      this.hashTags = request.getHashTags();
      this.type = request.getChannelType();
      this.userId = userId;
    }
  }

  @Getter
  @AllArgsConstructor
  public static class FindChannelDto {
    private final String type;
    private final Integer idx;
  }

  @Getter
  @AllArgsConstructor
  public static class FindMyChannelDto {
    private final String type;
    private final String userId;
    private final Integer idx;
  }

  @Getter
  @AllArgsConstructor
  public static class FindChannelByHashTagDto {
    private final String tagName;
    private final String type;
    private final Integer idx;
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


    public CreateChannelResponse(final Channel channel) {
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
  public static class FindAllChannelResponse {

    private List<ChannelResponse> channels;

    public FindAllChannelResponse(final List<Channel> channels) {
      this.channels = channels.stream().map(ChannelResponse::new).collect(toList());
    }
  }

  @Getter
  public static class ChannelResponse {

    private String id;
    private String channelName;
    private long limitParticipants;
    private long currentParticipants;
    private long timeToLive;
    private List<HashTagResponse> channelHashTags;
    private ChannelType channelType;

    public ChannelResponse(Channel channel) {
      this.id = channel.getId();
      this.channelName = channel.getChannelName();
      this.limitParticipants = channel.getLimitParticipants();
      this.currentParticipants = channel.getCurrentParticipants();
      this.timeToLive = channel.getTimeToLive();
      this.channelHashTags = channel.getChannelHashTags().stream().map(i -> {
        return new HashTagResponse(new TagNameResponse(i.getHashTag().getName()));
      }).collect(toList());
      this.channelType = channel.getChannelType();
    }
  }

  @Getter
  @AllArgsConstructor
  public static class ExtensionChannelTTLResponse {
    private long channelTTL;
  }
}

