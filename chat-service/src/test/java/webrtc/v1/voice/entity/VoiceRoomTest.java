package webrtc.v1.voice.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import webrtc.v1.staticgenarator.UserGenerator;
import webrtc.v1.staticgenarator.VoiceRoomGenerator;
import webrtc.v1.user.entity.Users;
import webrtc.v1.voice.exception.VoiceException.InvalidAccessToOpenViduServerException;

public class VoiceRoomTest {

  private static final String token = "VoiceRoom-Token#1";
  private static final String invalidToken = "Not Exist Token #1";

  @Test
  void VoiceRoom에유저추가성공() {
    // given
    VoiceRoom voiceRoom = VoiceRoomGenerator.createVoiceRoom();
    Users user = UserGenerator.createUsers();

    // when
    voiceRoom.addUser(user, token);

    // then

  }

  @Test
  void getUser성공() {
    // given
    VoiceRoom voiceRoom = VoiceRoomGenerator.createVoiceRoom();
    Users user = UserGenerator.createUsers();

    // when
    voiceRoom.addUser(user, token);

    // then
    assertThat(voiceRoom.getUsers().containsKey(user.getId())).isTrue();
  }

  @Test
  void getName성공() {
    // given
    VoiceRoom voiceRoom = VoiceRoomGenerator.createVoiceRoom();

    // when

    // then
    assertThat(voiceRoom.getName()).isEqualTo(VoiceRoomGenerator.getName());
  }

  @Test
  void getID성공() {
    // given
    VoiceRoom voiceRoom = VoiceRoomGenerator.createVoiceRoom();

    // when

    // then
    assertThat(voiceRoom.getId()).isEqualTo(VoiceRoomGenerator.getId());
  }

  @Test
  void valid토큰() {
    // given
    VoiceRoom voiceRoom = VoiceRoomGenerator.createVoiceRoom();
    Users user = UserGenerator.createUsers();
    voiceRoom.addUser(user, token);

    // when
    boolean isValid = voiceRoom.isValidToken(user.getId(), token);

    // then
    assertThat(isValid).isTrue();
  }

  @Test
  void inValid토큰() {
    // given
    VoiceRoom voiceRoom = VoiceRoomGenerator.createVoiceRoom();
    Users user = UserGenerator.createUsers();
    voiceRoom.addUser(user, token);

    // when

    // then
    assertThrows(InvalidAccessToOpenViduServerException.class,
        () -> voiceRoom.isValidToken(user.getId(), invalidToken));
  }

  @Test
  void 유저목록공백() {
    // given
    VoiceRoom voiceRoom = VoiceRoomGenerator.createVoiceRoom();

    // when
    boolean isEmpty = voiceRoom.isEmpty();

    // then
    assertThat(isEmpty).isTrue();
  }

  @Test
  void 유저목록공백아님() {
    // given
    VoiceRoom voiceRoom = VoiceRoomGenerator.createVoiceRoom();
    Users user = UserGenerator.createUsers();
    voiceRoom.addUser(user, token);

    // when
    boolean isEmpty = voiceRoom.isEmpty();

    // then
    assertThat(isEmpty).isFalse();
  }

  @Test
  void 유저토큰삭제성공() {
    // given
    VoiceRoom voiceRoom = VoiceRoomGenerator.createVoiceRoom();
    Users user = UserGenerator.createUsers();
    voiceRoom.addUser(user, token);

    // when
    voiceRoom.removeToken(user.getId());
    boolean isEmpty = voiceRoom.isEmpty();

    // then
    assertThat(isEmpty).isTrue();
  }

  @Test
  void 유저토큰삭제실패없는정보() {
    // given
    VoiceRoom voiceRoom = VoiceRoomGenerator.createVoiceRoom();
    Users user = UserGenerator.createUsers();

    // when
    voiceRoom.removeToken(user.getId());
    boolean isEmpty = voiceRoom.isEmpty();

    // then
    assertThat(isEmpty).isTrue();
  }
}
