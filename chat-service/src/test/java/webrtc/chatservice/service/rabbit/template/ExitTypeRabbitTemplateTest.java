package webrtc.chatservice.service.rabbit.template;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@Import({
        ExitTypeRabbitMessageTemplate.class
})
@ExtendWith(MockitoExtension.class)
public class ExitTypeRabbitTemplateTest {

    @InjectMocks
    private ExitTypeRabbitMessageTemplate messageTemplate;

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
