package webrtc.chatservice.service.rabbit.template;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static webrtc.chatservice.config.RabbitmqConfig.topicExchangeName;


import static webrtc.chatservice.config.RabbitmqConfig.*;

@Component
@RequiredArgsConstructor
public class CloseTypeRagbbitMessageTemplate extends RabbitMessageTemplate {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void routing(String message) {
        rabbitTemplate.convertAndSend(topicExchangeName, chatExitRoutingKey, message);
    }
}