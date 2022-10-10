package webrtc.chatservice.service.chat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.enums.SocketServerMessageType;
import webrtc.chatservice.service.chat.factory.ChattingMessageFactory;
import webrtc.chatservice.service.chat.factory.ChattingMessageFactoryImpl;

import java.util.ArrayList;

import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ClientMessageType.*;

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
        return chattingMessageFactory.createMessage(channel.getId(), type, nickname1, "test", 1L, new ArrayList<>(), idx, email1);
    }
}
