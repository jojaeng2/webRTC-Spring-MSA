package webrtc.v1.channel.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import webrtc.v1.staticgenarator.ChannelGenerator;
import webrtc.v1.staticgenarator.UserGenerator;
import webrtc.v1.user.entity.Users;

public class ChannelUserTest {

  @Test
  void getUsers성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();
    Users user = UserGenerator.createUsers();

    // when
    ChannelUser channelUser = ChannelUser.builder()
        .channel(channel)
        .user(user)
        .build();

    // then
    Assertions.assertThat(user).isEqualTo(channelUser.getUser());
  }

  @Test
  void getChannel성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();
    Users user = UserGenerator.createUsers();

    // when
    ChannelUser channelUser = ChannelUser.builder()
        .channel(channel)
        .user(user)
        .build();

    // then
    Assertions.assertThat(channel).isEqualTo(channelUser.getChannel());
  }




}
