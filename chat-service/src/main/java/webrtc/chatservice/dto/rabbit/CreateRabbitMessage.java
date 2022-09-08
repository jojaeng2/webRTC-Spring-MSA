package webrtc.chatservice.dto.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import webrtc.chatservice.dto.chat.ChattingMessage;

public abstract class CreateRabbitMessage {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void send(ChattingMessage serverMessage) {
        try {
            String message = objectMapper.writeValueAsString(serverMessage);
            routing(message);
        } catch (Exception e) {
            System.out.println("RabbitMessage Send Fail!!");
        }
    }

    public abstract void routing(String message);
}
