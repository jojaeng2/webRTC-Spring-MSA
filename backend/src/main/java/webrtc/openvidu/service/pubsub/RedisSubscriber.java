package webrtc.openvidu.service.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import webrtc.openvidu.dto.ChatDto.ChatServerMessage;
import webrtc.openvidu.dto.ChatDto.PublishMessage;
import webrtc.openvidu.dto.ChatDto.ServerNoticeMessage;
import webrtc.openvidu.enums.SocketServerMessageType;
import webrtc.openvidu.utils.CustomJsonMapper;

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
            System.out.println("RedisSubscriber onMessage Method");
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
            System.out.println("RedisSubscriber onMessage publishMessage = " + publishMessage);

            // String type message parsing to PublishMessage Object
            Object obj = CustomJsonMapper.jsonParse(publishMessage, PublishMessage.class);
            PublishMessage psmg = PublishMessage.class.cast(obj);

            PublishMessage serverMessage = new PublishMessage();
            SocketServerMessageType type = psmg.getType();
            if(type == null) {
                throw new NullPointerException("Not PublishMessage. \n 잘못된 형식의 메시지가 입력됨.");
            }
            switch(type) {
                case CHAT:
                    serverMessage = objectMapper.readValue(publishMessage, ChatServerMessage.class);
                    break;
                case RENEWAL:
                    serverMessage = objectMapper.readValue(publishMessage, ServerNoticeMessage.class);
                    break;
                case CLOSE:
                    break;
            }

            // WebSocket Subscriber들에게 message send
            messagingTemplate.convertAndSend("/sub/chat/room/" + serverMessage.getChannelId(), serverMessage);
        } catch (Exception e) {
            System.out.println("error in onMessage = " + e);
        }
    }
}
