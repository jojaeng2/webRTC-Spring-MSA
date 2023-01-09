package webrtc.v1.utils.response;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import webrtc.v1.staticgenarator.HttpStatusResponseGenerator;

public class HttpStatusResponseTest {

  @Test
  void getCode성공() {
    // given
    HttpStatusResponse response = HttpStatusResponseGenerator.createHttpStatusResponse();

    // when

    // then
    Assertions.assertThat(response.getCode()).isEqualTo(HttpStatusResponseGenerator.getCode());
  }

  @Test
  void getMessage성공() {
    // given
    HttpStatusResponse response = HttpStatusResponseGenerator.createHttpStatusResponse();

    // when

    // then
    Assertions.assertThat(response.getMessage()).isEqualTo(HttpStatusResponseGenerator.getMessage());
  }

}
