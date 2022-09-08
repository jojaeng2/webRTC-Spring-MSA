package webrtc.chatservice.dto.rabbit;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static webrtc.chatservice.config.RabbitmqConfig.chatEnterRoutingKey;
import static webrtc.chatservice.config.RabbitmqConfig.topicExchangeName;
import static webrtc.chatservice.config.RabbitmqConfig.*;


@Component
@RequiredArgsConstructor
public class ChatTypeRabbitMessage extends CreateRabbitMessage{
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void routing(String message) {
        rabbitTemplate.convertAndSend(topicExchangeName, chatTextRoutingKey, message);
    }
}