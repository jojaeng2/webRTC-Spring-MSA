package webrtc.v1.hashtag.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import webrtc.v1.channel.entity.ChannelHashTag;
import webrtc.v1.staticgenarator.ChannelHashTagGenerator;
import webrtc.v1.staticgenarator.HashTagGenerator;

public class HashTagTest {

  @Test
  void getChannelHashTags성공() {
    // given
    HashTag hashTag = HashTagGenerator.createHashTag();
    ChannelHashTag channelHashTag = ChannelHashTagGenerator.createChannelHashTag();
    hashTag.addChannelHashTag(channelHashTag);

    // when

    // then
    assertThat(hashTag.getChannelHashTags().get(0)).isEqualTo(channelHashTag);
  }

}
