package webrtc.v1.point.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import webrtc.v1.staticgenarator.PointGenerator;
import webrtc.v1.staticgenarator.UserGenerator;
import webrtc.v1.user.entity.Users;

public class PointTest {

  @Test
  void getUser성공() {
    // given
    Point point = PointGenerator.createPoint();
    Users user = UserGenerator.createUsers();
    // when

    // then
    assertThat(point.getUser().getEmail()).isEqualTo(user.getEmail());
  }

  @Test
  void getMessage성공() {
    // given
    Point point = PointGenerator.createPoint();
    // when

    // then
    assertThat(point.getMessage()).isEqualTo(PointGenerator.getMessage());
  }

  @Test
  void getCreated() {
    // given
    Point point = PointGenerator.createPoint();

    // when

    // then
    assertThat(point.getCreated_at()).isNotNull();
  }
}
