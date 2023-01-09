package webrtc.v1.voice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import io.openvidu.java.client.OpenVidu;
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
import webrtc.v1.voice.entity.VoiceRoom;
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

    doThrow(NotExistUserException.class)
        .when(usersRepository).findById(any(String.class));

    // when

    // then
    Assertions.assertThrows(NotExistUserException.class,
        () -> voiceRoomService.getToken(getTokenRequest(), user.getId()));
  }

  @Test
  void 토큰발급성공음성채팅방있음() {
    // given
    VoiceRoom voiceRoom = VoiceRoomGenerator.createVoiceRoom();

//    doReturn(Optional.of(voiceRoom))
//        .when(voiceRoomRepository).findById(any(String.class));
//    doReturn(token)
//        .when(openVidu)
//        .getActiveSessions()
//        .stream()
//        .filter(session -> is)
    // when

    // then
  }

  private GetTokenRequest getTokenRequest() {
    return new GetTokenRequest(ChannelGenerator.createTextChannel().getId());
  }


}
