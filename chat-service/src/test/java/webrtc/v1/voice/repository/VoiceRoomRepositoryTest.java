package webrtc.v1.voice.repository;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import webrtc.v1.staticgenarator.VoiceRoomGenerator;
import webrtc.v1.voice.entity.VoiceRoom;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VoiceRoomRepositoryTest {

  @Autowired
  private VoiceRoomRepository voiceRoomRepository;

  @After
  public void tearDown() throws Exception {
    voiceRoomRepository.deleteAll();
  }

  @Test
  public void save성공() {
    // given
    VoiceRoom voiceRoom = VoiceRoomGenerator.createVoiceRoom();

    // when
    voiceRoomRepository.save(voiceRoom.getId(), voiceRoom);

    // then

  }

  @Test
  public void findById성공() {
    // given
    VoiceRoom voiceRoom = VoiceRoomGenerator.createVoiceRoom();
    voiceRoomRepository.save(voiceRoom.getId(), voiceRoom);
    voiceRoomRepository.save(voiceRoom.getId() + "2", voiceRoom);
    // when
    VoiceRoom findVoiceRoom = voiceRoomRepository.findById(voiceRoom.getId()).get();

    // then
    Assertions.assertThat(findVoiceRoom.getId()).isEqualTo(voiceRoom.getId());
  }
}
