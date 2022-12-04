package webrtc.v1.service.chat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.user.entity.Users;
import webrtc.v1.chat.dto.ClientMessage;
import webrtc.v1.channel.enums.ChannelType;
import webrtc.v1.chat.enums.ClientMessageType;
import webrtc.v1.channel.exception.ChannelException.NotExistChannelException;
import webrtc.v1.channel.service.ChannelIOService;
import webrtc.v1.chat.service.factory.SocketMessageFactoryImpl;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static webrtc.v1.channel.enums.ChannelType.TEXT;
import static webrtc.v1.chat.enums.ClientMessageType.*;
import static webrtc.v1.chat.enums.ClientMessageType.CREATE;

@ExtendWith(MockitoExtension.class)
@Import(SocketMessageFactoryImpl.class)
public class SocketMessageFactoryTest {


    @Mock
    private ChannelIOService channelIOService;

    @InjectMocks
    private SocketMessageFactoryImpl socketMessageFactory;

    ClientMessageType chat = CHAT;
    ClientMessageType enter = ENTER;
    ClientMessageType exit = EXIT;
    ClientMessageType close = CLOSE;
    ClientMessageType reenter = REENTER;
    ClientMessageType create = CREATE;

    String nickname1 = "nickname1";
    String password = "password";
    String email1 = "email1";
    String channelName1 = "channelName1";
    ChannelType text = TEXT;

    @Test
    void CHAT메시지전송성공() {
        // given
        ClientMessage message = new ClientMessage();
        message.setMessage("CHAT");
        message.setSenderName(null);

        // when
        createSocketMessage(chat, message);

        // then
        assertThat(message.getSenderName()).isEqualTo(nickname1);
        assertThat(message.getMessage()).isEqualTo("CHAT");
    }

    @Test
    void EXIT메시지전송성공() {
        ClientMessage message = new ClientMessage();
        message.setSenderName(nickname1);

        doNothing()
                .when(channelIOService).exitChannel(any(String.class), any(UUID.class));

        // when
        createSocketMessage(exit, message);

        // then
        assertThat(message.getSenderName()).isEqualTo(nickname1);
        assertThat(message.getMessage()).isEqualTo("[알림] " + nickname1 + " 님이 채팅방에서 퇴장했습니다.");
    }

    @Test
    void EXIT메시지전송실패() {
        ClientMessage message = new ClientMessage();
        message.setMessage("CHAT");
        message.setSenderName(nickname1);

        doThrow(new NotExistChannelException())
                .when(channelIOService).exitChannel(any(String.class), any(UUID.class));

        // when


        // then
        assertThrows(NotExistChannelException.class,
                () -> createSocketMessage(exit, message));
    }

    @Test
    void ENTER메시지전송성공() {
        // given
        ClientMessage message = new ClientMessage();
        message.setMessage(null);
        message.setSenderName(nickname1);

        // when
        createSocketMessage(enter, message);
        // then
        assertThat(message.getSenderName()).isEqualTo(nickname1);
        assertThat(message.getMessage()).isEqualTo("[알림] " + nickname1 + " 님이 채팅방에 입장했습니다.");
    }

    private Channel createChannel(String name, ChannelType type) {
        return Channel.builder()
                .channelName(name)
                .channelType(type)
                .build();
    }

    private Users createUser(String nickname, String password, String email) {
        return Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();
    }

    private void createSocketMessage(ClientMessageType type, ClientMessage message) {
        socketMessageFactory.messageFactoryConst();
        Channel channel = createChannel(channelName1, text);
        Users user = createUser(nickname1, password, email1);
        socketMessageFactory.execute(type, message, user, channel.getId());
    }
}
