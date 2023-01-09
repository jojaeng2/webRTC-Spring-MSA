package webrtc.v1.voice.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import webrtc.v1.voice.dto.VoiceRoomDto.GetTokenRequest;

public class VoiceRoomDtoTest {

  private static final String id = "channelId#1";

  @Test
  void getTokenRequest생성() {
    // given

    // when
    GetTokenRequest getTokenRequest = new GetTokenRequest(id);

    // then
    Assertions.assertThat(getTokenRequest.getChannelId()).isEqualTo(id);
  }

  @Test
  void VoiceRoomDto생성() {
    // given
    VoiceRoomDto voiceRoomDto = new VoiceRoomDto();
    // when

    // then

  }


}
