package webrtc.v1.chat.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import webrtc.v1.staticgenarator.ChattingMessageGenerator;

public class ChattingMessageTest {

  @Test
  void getChannelId성공() {
    // given
    ChattingMessage chattingMessage = ChattingMessageGenerator.createChattingMessage();

    // when

    // then
    Assertions.assertThat(chattingMessage.getChannelId()).isNotNull();
  }

  @Test
  void getSenderName성공() {
    // given
    ChattingMessage chattingMessage = ChattingMessageGenerator.createChattingMessage();

    // when

    // then
    Assertions.assertThat(chattingMessage.getSenderName()).isNotNull();
  }

  @Test
  void getChatMessage성공() {
    // given
    ChattingMessage chattingMessage = ChattingMessageGenerator.createChattingMessage();

    // when

    // then
    Assertions.assertThat(chattingMessage.getChatMessage()).isNotNull();
  }

  @Test
  void getParticipants성공() {
    // given
    ChattingMessage chattingMessage = ChattingMessageGenerator.createChattingMessage();

    // when

    // then
    Assertions.assertThat(chattingMessage.getCurrentParticipants()).isNotNull();
  }

  @Test
  void getUsers성공() {
    // given
    ChattingMessage chattingMessage = ChattingMessageGenerator.createChattingMessage();

    // when

    // then
    Assertions.assertThat(chattingMessage.getUsers()).isNotNull();
  }

  @Test
  void getLogId성공() {
    // given
    ChattingMessage chattingMessage = ChattingMessageGenerator.createChattingMessage();

    // when

    // then
    Assertions.assertThat(chattingMessage.getLogId()).isNotNull();
  }

  @Test
  void getSenderEmail성공() {
    // given
    ChattingMessage chattingMessage = ChattingMessageGenerator.createChattingMessage();

    // when

    // then
    Assertions.assertThat(chattingMessage.getSenderEmail()).isNotNull();
  }

  @Test
  void getTime성공() {
    // given
    ChattingMessage chattingMessage = ChattingMessageGenerator.createChattingMessage();

    // when

    // then
    Assertions.assertThat(chattingMessage.getSendTime()).isNotNull();
  }
}
