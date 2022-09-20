package webrtc.chatservice.service.rabbit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.service.rabbit.factory.RabbitMessageFactory;
import webrtc.chatservice.service.rabbit.factory.RabbitMessageFactoryImpl;
import webrtc.chatservice.service.rabbit.template.*;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ClientMessageType.*;

@Import({
        RabbitMessageFactoryImpl.class
})
@ExtendWith(MockitoExtension.class)
public class RabbitMessageFactoryImplTest {

    @InjectMocks
    private RabbitMessageFactoryImpl rabbitMessageFactory;

    @Mock
    private EnterTypeRabbitMessageTemplate enterTypeRabbitMessage;
    @Mock
    private CreateTypeRabbitMessageTemplate createTypeRabbitMessageTemplate;
    @Mock
    private ChatTypeRabbitMessageTemplate chatTypeRabbitMessage;
    @Mock
    private ExitTypeRabbitMessageTemplate exitTypeRabbitMessage;
    @Mock
    private CloseTypeRagbbitMessageTemplate closeTypeRabbitMessage;


    String nickname1 = "nickname1";
    String email1 = "email1";
    String channelName1 = "channelName1";
    ChannelType text = TEXT;

    ClientMessageType enter = ENTER;
    ClientMessageType create = CREATE;
    ClientMessageType chat = CHAT;
    ClientMessageType exit = EXIT;
    ClientMessageType close = CLOSE;


    Long idx = 1L;

    @Test
    void ENTER메시지전송() {
        // given
        rabbitMessageFactory.messageFactoryConst();
        doNothing()
                .when(enterTypeRabbitMessage).send(any(ChattingMessage.class));

        ChattingMessage message = createChattingMessage();

        // when

        // then
        rabbitMessageFactory.execute(message, enter);
    }

    @Test
    void CREATE메시지전송() {
        // given
        rabbitMessageFactory.messageFactoryConst();
        doNothing()
                .when(createTypeRabbitMessageTemplate).send(any(ChattingMessage.class));

        ChattingMessage message = createChattingMessage();

        // when

        // then
        rabbitMessageFactory.execute(message, create);
    }

    @Test
    void CHAT메시지전송() {
        // given
        rabbitMessageFactory.messageFactoryConst();
        doNothing()
                .when(chatTypeRabbitMessage).send(any(ChattingMessage.class));

        ChattingMessage message = createChattingMessage();

        // when

        // then
        rabbitMessageFactory.execute(message, chat);
    }

    @Test
    void EXIT메시지전송() {
        // given
        rabbitMessageFactory.messageFactoryConst();
        doNothing()
                .when(exitTypeRabbitMessage).send(any(ChattingMessage.class));

        ChattingMessage message = createChattingMessage();

        // when

        // then
        rabbitMessageFactory.execute(message, exit);
    }

    @Test
    void CLOSE메시지전송() {
        // given
        rabbitMessageFactory.messageFactoryConst();
        doNothing()
                .when(closeTypeRabbitMessage).send(any(ChattingMessage.class));

        ChattingMessage message = createChattingMessage();

        // when

        // then
        rabbitMessageFactory.execute(message, close);
    }

    private Channel createChannel(String name, ChannelType type) {
        return new Channel(name, type);
    }

    private ChattingMessage createChattingMessage() {
        Channel channel = createChannel(channelName1, text);
        return new ChattingMessage(channel.getId(), nickname1, "message", 1L, new ArrayList<>(), 1L, email1);
    }
}
