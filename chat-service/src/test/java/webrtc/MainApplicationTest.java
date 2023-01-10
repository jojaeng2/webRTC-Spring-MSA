package webrtc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import webrtc.v1.MainApplication;

public class MainApplicationTest {

  @Test
  void testApplication() {
    MockedStatic<SpringApplication> utilities = Mockito.mockStatic(SpringApplication.class);
    utilities.when((MockedStatic.Verification) SpringApplication.run(MainApplication.class, new String[]{})).thenReturn(null);
    MainApplication.main(new String[]{});
    assertThat(SpringApplication.run(MainApplication.class, new String[]{})).isEqualTo(null);
  }
}

