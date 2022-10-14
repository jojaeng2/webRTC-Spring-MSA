package webrtc.chatservice.service.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import webrtc.chatservice.dto.chat.ChattingMessage;
import webrtc.chatservice.utils.json.CustomJsonMapper;

@RequiredArgsConstructor
@Service
@Slf4j
public class RedisSubscriberImpl implements RedisSubscriber {
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * Redis에서 메시지가 발행되면, 대기하고 있던 onMessage가 해당 메시지를 처리
     */
    public void sendMessage(String chatMessage) {
        try {
            ChattingMessage publishMessage = objectMapper.readValue(chatMessage, ChattingMessage.class);

            // WebSocket Subscriber들에게 message send
            messagingTemplate.convertAndSend("/sub/chat/room/" + publishMessage.getChannelId(), publishMessage);
        } catch (Exception e) {
            System.out.println("error in onMessage = " + e);
        }
    }
}
