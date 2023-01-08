package webrtc.v1.staticgenarator;

import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.entity.ChannelUser;
import webrtc.v1.user.entity.Users;

public class ChannelUserGenerator {

  private static final Channel channel = ChannelGenerator.createTextChannel();
  private static final Users user = UserGenerator.createUsers();

  private ChannelUserGenerator() {}

  public static ChannelUser createChannelUser() {
    return ChannelUser.builder()
        .channel(channel)
        .user(user)
        .build();
  }

}
