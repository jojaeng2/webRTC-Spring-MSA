package webrtc.v1.channel.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import webrtc.v1.channel.dto.ChannelDto.CreateChannelResponse;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.staticgenarator.ChannelGenerator;

public class ChannelDtoTest {

  @Test
  void CreateChannelResponse에서Id조회성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();
    CreateChannelResponse response = new CreateChannelResponse(channel);

    // when

    // then
    Assertions.assertThat(response.getId()).isEqualTo(channel.getId());
  }

  @Test
  void CreateChannelResponse에서Type조회성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();
    CreateChannelResponse response = new CreateChannelResponse(channel);

    // when

    // then
    Assertions.assertThat(response.getChannelType()).isEqualTo(channel.getChannelType());
  }

  @Test
  void CreateChannelResponse에서채널유저조회성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();
    CreateChannelResponse response = new CreateChannelResponse(channel);

    // when

    // then
    Assertions.assertThat(response.getChannelUsers()).isEqualTo(channel.getChannelUsers());
  }

  @Test
  void ChannelDto생성() {
    ChannelDto dto = new ChannelDto();

  }

}
