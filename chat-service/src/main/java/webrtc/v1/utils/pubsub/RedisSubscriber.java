package webrtc.v1.utils.pubsub;

public interface RedisSubscriber {

    void sendMessage(String chatMessage);
}
