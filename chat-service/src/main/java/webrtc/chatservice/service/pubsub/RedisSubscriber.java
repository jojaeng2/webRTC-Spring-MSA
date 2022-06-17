package webrtc.chatservice.service.pubsub;

public interface RedisSubscriber {

    void sendMessage(String chatMessage);
}
