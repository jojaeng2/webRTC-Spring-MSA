package webrtc.chatservice.service.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import webrtc.chatservice.config.RabbitmqConfig;
import webrtc.chatservice.dto.ChatDto;
import webrtc.chatservice.dto.ChatDto.ChatServerMessage;
import webrtc.chatservice.enums.ClientMessageType;

import static webrtc.chatservice.config.RabbitmqConfig.*;

@Component
@RequiredArgsConstructor
public class RabbitPublishImpl implements RabbitPublish {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;


    public void publishMessage(ChatServerMessage serverMessage, ClientMessageType type) {
        // try {
        //     String message = objectMapper.writeValueAsString(serverMessage);
        //     switch (type) {
        //         case ENTER:
        //         case CREATE:
        //             rabbitTemplate.convertAndSend(topicExchangeName, chatEnterRoutingKey, message);
        //             break;
        //         case CHAT:
        //             rabbitTemplate.convertAndSend(topicExchangeName, chatTextRoutingKey, message);
        //             break;
        //         case EXIT:
        //         case CLOSE:
        //             rabbitTemplate.convertAndSend(topicExchangeName, chatExitRoutingKey, message);
        //             break;
        //     }
        // } catch (Exception e) {
        //     System.out.println("RabbitMessage Send Fail!!");
        // }
    }
}
