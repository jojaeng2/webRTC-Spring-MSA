package webrtc.v1.channel.exception;

import static webrtc.v1.chat.enums.SocketInterceptorErrorType.ALREADY_FULL_CHANNEL;

import org.junit.jupiter.api.Test;
import webrtc.v1.channel.exception.ChannelException.ChannelExceptionDto;

public class ChannelExceptionTest {

  @Test
  void ChannelExceptionDtoTest() {
    // given

    // when
    ChannelExceptionDto channelExceptionDto = new ChannelExceptionDto(ALREADY_FULL_CHANNEL,
        "채널에 인원이 가득차 입장할 수없습니다.", 0);
    // then

  }
}
