package webrtc.openvidu.service.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import webrtc.openvidu.dto.ChatDto.ChatServerMessage;

@RequiredArgsConstructor
@Service
public class RedisSubscriberImpl implements RedisSubscriber {
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * Redis에서 메시지가 발행되면, 대기하고 있던 onMessage가 해당 메시지를 처리
     */
    public void sendMessage(String chatMessage) {
        try {
            ChatServerMessage publishMessage = objectMapper.readValue(chatMessage, ChatServerMessage.class);
            // WebSocket Subscriber들에게 message send
            messagingTemplate.convertAndSend("/sub/chat/room/" + publishMessage.getChannelId(), publishMessage);
        } catch (Exception e) {
            System.out.println("error in onMessage = " + e);
        }
    }
}
