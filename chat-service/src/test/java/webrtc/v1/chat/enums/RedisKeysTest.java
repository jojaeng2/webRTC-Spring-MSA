package webrtc.v1.chat.enums;

import static webrtc.v1.chat.enums.RedisKeys.BLANK;
import static webrtc.v1.chat.enums.RedisKeys.CHAT_LOG;
import static webrtc.v1.chat.enums.RedisKeys.LAST_INDEX;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class RedisKeysTest {

  @Test
  void CHAT_LOGkey성공() {
    // given
    String prefix = CHAT_LOG.getPrefix();

    // when

    // then
    Assertions.assertThat(prefix).isEqualTo("LOG_");
  }

  @Test
  void LAST_INDEXkey성공() {
    // given
    String prefix = LAST_INDEX.getPrefix();

    // when

    // then
    Assertions.assertThat(prefix).isEqualTo("LAST_INDEX_");
  }

  @Test
  void BLANKkey성공() {
    // given
    String prefix = BLANK.getPrefix();

    // when

    // then
    Assertions.assertThat(prefix).isEqualTo("_");
  }
}
