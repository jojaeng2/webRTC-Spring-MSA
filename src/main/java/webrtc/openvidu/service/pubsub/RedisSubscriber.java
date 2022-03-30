package webrtc.openvidu.service.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import webrtc.openvidu.dto.ChatDto.ServerMessage;

@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * @param message
     * @param pattern
     * Redis에서 메시지가 발행되면, 대기하고 있던 onMessage가 해당 메시지를 처리
     */

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            //redis에서 발행된 데이터를 받아 deserialize

            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());

            // ServerMessage 객체로 매핑
            ServerMessage serverMessage = objectMapper.readValue(publishMessage, ServerMessage.class);
            messagingTemplate.convertAndSend("/sub/chat/room" + serverMessage.getChannelId(), serverMessage);
        } catch (Exception e) {
            System.out.println("error in onMessage = " + e);
        }
    }
}
