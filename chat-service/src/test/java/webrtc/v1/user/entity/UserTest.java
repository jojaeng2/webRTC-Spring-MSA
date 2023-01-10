package webrtc.v1.user.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.staticgenarator.UserGenerator;

public class UserTest {

  @Test
  void getCreatedAt성공() {
    // given
    Users user = UserGenerator.createUsers();

    // when

    // then
    Assertions.assertThat(user.getCreated_at()).isNotNull();
  }

  @Test
  void getUpdatedAt성공() {
    // given
    Users user = UserGenerator.createUsers();

    // when

    // then
    Assertions.assertThat(user.getUpdated_at()).isNotNull();
  }

  @Test
  void getBirthday성공() {
    // given
    Users user = UserGenerator.createUsers();

    // when

    // then
    Assertions.assertThat(user.getBirthdate()).isNotNull();
  }

  @Test
  void getPhoneNumber성공() {
    // given
    Users user = UserGenerator.createUsers();

    // when

    // then
    Assertions.assertThat(user.getPhone_number()).isNotNull();
  }

  @Test
  void getSchool성공() {
    // given
    Users user = UserGenerator.createUsers();

    // when

    // then
    Assertions.assertThat(user.getSchool()).isNotNull();
  }

  @Test
  void getCompany성공() {
    // given
    Users user = UserGenerator.createUsers();

    // when

    // then
    Assertions.assertThat(user.getCompany()).isNotNull();
  }

  @Test
  void getNickName성공() {
    // given
    Users user = UserGenerator.createUsers();

    // when

    // then
    Assertions.assertThat(user.getNickname()).isNotNull();
  }

  @Test
  void getNicknameExpire성공() {
    // given
    Users user = UserGenerator.createUsers();

    // when

    // then
    Assertions.assertThat(user.getNickname_expire_at()).isNotNull();
  }

  @Test
  void getIsAdmin성공() {
    // given
    Users user = UserGenerator.createUsers();

    // when

    // then
    Assertions.assertThat(user.getIs_admin()).isNotNull();
  }

  @Test
  void getChannelUser성공() {
    // given
    Users user = UserGenerator.createUsers();

    // when

    // then
    Assertions.assertThat(user.getChannelUsers()).isNotNull();
  }
}
