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
        TypeRabbitMessageTemplate.class
})
@ExtendWith(MockitoExtension.class)
public class TypeRabbitMessageTemplateTest {

    @InjectMocks
    private TypeRabbitMessageTemplate typeRabbitMessageTemplate;

    @Mock
    private RabbitTemplate rabbitTemplate;
    String message = "message";

    @Test
    void 라우팅성공() {
        // given
        doNothing()
                .when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(String.class));

        // when

        // then
        typeRabbitMessageTemplate.routing(message);
    }
}
