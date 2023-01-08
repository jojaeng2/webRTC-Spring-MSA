package webrtc.v1.staticgenarator;

import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.entity.ChannelHashTag;
import webrtc.v1.hashtag.entity.HashTag;
import webrtc.v1.user.entity.Users;

public class ChannelHashTagGenerator {

  private static final Channel channel = ChannelGenerator.createTextChannel();
  private static final HashTag hashTag = HashTagGenerator.createHashTag();

  private ChannelHashTagGenerator() {}

  public static ChannelHashTag createChannelHashTag() {
    return ChannelHashTag.builder().channel(channel)
        .hashTag(hashTag).build();
  }


}
