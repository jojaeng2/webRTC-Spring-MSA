package webrtc.openvidu.service.pubsub;

public interface RedisSubscriber {

    void sendMessage(String chatMessage);
}
