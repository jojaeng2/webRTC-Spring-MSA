package webrtc.v1.service.chat;

import static webrtc.v1.channel.enums.ChannelType.TEXT;
import static webrtc.v1.chat.enums.ClientMessageType.CHAT;
import static webrtc.v1.chat.enums.ClientMessageType.CLOSE;
import static webrtc.v1.chat.enums.ClientMessageType.CREATE;
import static webrtc.v1.chat.enums.ClientMessageType.ENTER;
import static webrtc.v1.chat.enums.ClientMessageType.EXIT;
import static webrtc.v1.chat.enums.ClientMessageType.REENTER;

import java.util.ArrayList;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.enums.ChannelType;
import webrtc.v1.chat.dto.ChattingMessage;
import webrtc.v1.chat.enums.ClientMessageType;
import webrtc.v1.chat.enums.SocketServerMessageType;
import webrtc.v1.chat.service.factory.ChattingMessageFactoryImpl;
import webrtc.v1.user.entity.Users;

@Import(ChattingMessageFactoryImpl.class)
public class ChattingMessageFactoryTest {

  private final ChattingMessageFactoryImpl chattingMessageFactory = new ChattingMessageFactoryImpl();

  ClientMessageType chat = CHAT;
  ClientMessageType enter = ENTER;
  ClientMessageType exit = EXIT;
  ClientMessageType close = CLOSE;
  ClientMessageType reenter = REENTER;
  ClientMessageType create = CREATE;

  String nickname1 = "nickname1";
  String email1 = "email1";
  String channelName1 = "channelName1";
  ChannelType text = TEXT;
  Long idx = 1L;

  @Test
  void CHAT메시지생성성공() {
    // given

    // when
    ChattingMessage message = createChattingMessage(chat);

    // then
    Assertions.assertThat(message.getType()).isLessThanOrEqualTo(SocketServerMessageType.CHAT);
  }

  @Test
  void ENTER메시지생성성공() {

    // given

    // when
    ChattingMessage message = createChattingMessage(enter);

    // then
    Assertions.assertThat(message.getType()).isLessThanOrEqualTo(SocketServerMessageType.RENEWAL);
  }

  @Test
  void EXIT메시지생성성공() {

    // given

    // when
    ChattingMessage message = createChattingMessage(exit);

    // then
    Assertions.assertThat(message.getType()).isLessThanOrEqualTo(SocketServerMessageType.RENEWAL);
  }

  @Test
  void CLOSE메시지생성성공() {

    // given

    // when
    ChattingMessage message = createChattingMessage(close);

    // then
    Assertions.assertThat(message.getType()).isLessThanOrEqualTo(SocketServerMessageType.CLOSE);
  }

  @Test
  void REENTER메시지생성성공() {

    // given

    // when
    ChattingMessage message = createChattingMessage(reenter);

    // then
    Assertions.assertThat(message.getType()).isLessThanOrEqualTo(SocketServerMessageType.RENEWAL);
  }

  @Test
  void CREATE메시지생성성공() {

    // given

    // when
    ChattingMessage message = createChattingMessage(create);

    // then
    Assertions.assertThat(message.getType()).isLessThanOrEqualTo(SocketServerMessageType.CREATE);
  }

  private Channel createChannel(String name, ChannelType type) {
    return Channel.builder()
        .channelName(name)
        .channelType(type)
        .build();
  }

  private ChattingMessage createChattingMessage(ClientMessageType type) {
    chattingMessageFactory.messageFactoryConst();
    Channel channel = createChannel(channelName1, text);
    return chattingMessageFactory.createMessage(channel, type, "test", new ArrayList<>(), idx,
        createUsers());
  }

  private Users createUsers() {
    return Users.builder()
        .email(email1)
        .nickname(nickname1)
        .build();
  }
}
