package webrtc.v1.service.pubsub;

public interface RedisSubscriber {

    void sendMessage(String chatMessage);
}
