package webrtc.chatservice.service.rabbit.template;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Import;
import webrtc.chatservice.service.rabbit.factory.RabbitMessageFactoryImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@Import({
        ChatTypeRabbitMessageTemplate.class
})
@ExtendWith(MockitoExtension.class)
public class ChatTypeRabbitTemplateTest {

    @InjectMocks
    private ChatTypeRabbitMessageTemplate messageTemplate;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Test
    void 라우팅성공() {
        // given
        doNothing()
                .when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(String.class));
        // when

        // then
        messageTemplate.routing("message");
    }
}
