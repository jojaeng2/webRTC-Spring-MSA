package webrtc.v1.channel.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import webrtc.v1.hashtag.entity.HashTag;
import webrtc.v1.staticgenarator.ChannelGenerator;
import webrtc.v1.staticgenarator.HashTagGenerator;

public class ChannelHashTagTest {

  @Test
  void ChannelHashTag로_채널조회성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();
    HashTag hashTag = HashTagGenerator.createHashTag();

    // when
    ChannelHashTag channelHashTag = ChannelHashTag.builder()
        .channel(channel)
        .hashTag(hashTag)
        .build();

    // then
    Assertions.assertThat(channel).isEqualTo(channelHashTag.getChannel());
  }

  @Test
  void ChannelHashTag로_해시태그조회성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();
    HashTag hashTag = HashTagGenerator.createHashTag();

    // when
    ChannelHashTag channelHashTag = ChannelHashTag.builder()
        .channel(channel)
        .hashTag(hashTag)
        .build();

    // then
    Assertions.assertThat(hashTag).isEqualTo(channelHashTag.getHashTag());
  }
}
