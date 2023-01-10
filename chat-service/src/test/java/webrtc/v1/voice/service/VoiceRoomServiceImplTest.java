package webrtc.v1.voice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.Session;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import webrtc.v1.staticgenarator.ChannelGenerator;
import webrtc.v1.staticgenarator.UserGenerator;
import webrtc.v1.staticgenarator.VoiceRoomGenerator;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.user.repository.UsersRepository;
import webrtc.v1.voice.dto.VoiceRoomDto.GetTokenRequest;
import webrtc.v1.voice.dto.VoiceRoomDto.RemoveUserInSessionRequest;
import webrtc.v1.voice.entity.VoiceRoom;
import webrtc.v1.voice.exception.VoiceException.AlreadyRemovedSessionInOpenViduServer;
import webrtc.v1.voice.repository.VoiceRoomRepository;

@ExtendWith(MockitoExtension.class)
public class VoiceRoomServiceImplTest {


  @InjectMocks
  private VoiceRoomServiceImpl voiceRoomService;

  @Mock
  private VoiceRoomRepository voiceRoomRepository;
  @Mock
  private UsersRepository usersRepository;
  @Mock
  private OpenVidu openVidu;

  private final String token = "token";

  @Test
  void 토큰발급실패유저없음() {
    // given

    Users user = UserGenerator.createUsers();

    doReturn(Optional.empty())
        .when(usersRepository).findById(any(String.class));

    // when

    // then
    Assertions.assertThrows(NotExistUserException.class,
        () -> voiceRoomService.getToken(getTokenRequest(), user.getId()));
  }

//  @Test
//  void 토큰발급성공방있음() {
//    // given
//
//    Users user = UserGenerator.createUsers();
//    VoiceRoom voiceRoom = VoiceRoomGenerator.createVoiceRoom();
//    doReturn(Optional.of(user))
//        .when(usersRepository).findById(any(String.class));
//    doReturn(Optional.of(voiceRoom))
//        .when(voiceRoomRepository).findById(any(String.class));
//
//    // when
//
//    // then
//    Assertions.assertThrows(NotExistUserException.class,
//        () -> voiceRoomService.getToken(getTokenRequest(), user.getId()));
//  }
//
//  @Test
//  void 토큰발급성공방없음() {
//    // given
//
//    Users user = UserGenerator.createUsers();
//
//    doReturn(Optional.of(user))
//        .when(usersRepository).findById(any(String.class));
//    doReturn(Optional.empty())
//        .when(voiceRoomRepository).findById(any(String.class));
//    // when
//
//    // then
//    Assertions.assertThrows(NotExistUserException.class,
//        () -> voiceRoomService.getToken(getTokenRequest(), user.getId()));
//  }

//  @Test
//  void 토큰발급성공음성채팅방없음() {
//    // given
//    VoiceRoom voiceRoom = VoiceRoomGenerator.createVoiceRoom();
//
//    doReturn(Optional.of(voiceRoom))
//        .when(voiceRoomRepository).findById(any(String.class));
//    doReturn(token)
//        .when(openVidu)
//        .getActiveSessions()
//        .stream()
//        .filter(session -> is)
//     //when
//
//     //then
//  }

  @Test
  void VoiceRoom에서회원삭제실패방없음() {
    // given
    Users user = UserGenerator.createUsers();
    doReturn(Optional.empty())
        .when(voiceRoomRepository).findById(any(String.class));

    // when

    // then
    Assertions.assertThrows(AlreadyRemovedSessionInOpenViduServer.class,
        () -> voiceRoomService.removeUserInVoiceRoom(getRemoveUserInSessionRequest(), user.getId()));
  }

  @Test
  void VoiceRoom삭제토큰있음() {
    // given
    Users user = UserGenerator.createUsers();
    VoiceRoom voiceRoom = VoiceRoomGenerator.createVoiceRoom();
    voiceRoom.addUser(user, token);
    doReturn(Optional.of(voiceRoom))
        .when(voiceRoomRepository).findById(any(String.class));
    doNothing()
        .when(voiceRoomRepository).update(any(String.class), any(VoiceRoom.class));

    // when
    voiceRoomService.removeUserInVoiceRoom(getRemoveUserInSessionRequest(), user.getId());
    // then
  }

  @Test
  void 토큰없음() {
    // given
    Users user = UserGenerator.createUsers();
    VoiceRoom voiceRoom = VoiceRoomGenerator.createVoiceRoom();
    doReturn(Optional.of(voiceRoom))
        .when(voiceRoomRepository).findById(any(String.class));

    // when
    voiceRoomService.removeUserInVoiceRoom(getRemoveUserInSessionRequest(), user.getId());
    // then
  }

  @Test
  void voiceRoom삭제실패유저있음() {
    // given
    Users user1 = UserGenerator.createUsers();
    Users user2 = UserGenerator.createUsers();
    VoiceRoom voiceRoom = VoiceRoomGenerator.createVoiceRoom();
    voiceRoom.addUser(user1, token);
    voiceRoom.addUser(user2, token);
    doReturn(Optional.of(voiceRoom))
        .when(voiceRoomRepository).findById(any(String.class));

    // when
    voiceRoomService.removeUserInVoiceRoom(getRemoveUserInSessionRequest(), user1.getId());
    // then
  }


  private GetTokenRequest getTokenRequest() {
    return new GetTokenRequest(ChannelGenerator.createTextChannel().getId());
  }

  private RemoveUserInSessionRequest getRemoveUserInSessionRequest() {
    return new RemoveUserInSessionRequest(ChannelGenerator.createTextChannel().getId(), token);
  }


}
