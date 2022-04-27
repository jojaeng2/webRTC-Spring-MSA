package webrtc.openvidu.config;

<<<<<<< HEAD:backend/src/main/java/webrtc/openvidu/config/WebSockConfig.java
import org.springframework.context.annotation.Configuration;
=======
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.messaging.simp.config.ChannelRegistration;
>>>>>>> 18cdb6bb39d5ef24329da9b5e0f6d53d239ef993:src/main/java/webrtc/openvidu/config/WebSockConfig.java
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
<<<<<<< HEAD:backend/src/main/java/webrtc/openvidu/config/WebSockConfig.java
=======
import webrtc.openvidu.handler.StompHandler;
import webrtc.openvidu.service.pubsub.RedisSubscriber;
>>>>>>> 18cdb6bb39d5ef24329da9b5e0f6d53d239ef993:src/main/java/webrtc/openvidu/config/WebSockConfig.java

@Configuration
@EnableWebSocketMessageBroker
public class WebSockConfig implements WebSocketMessageBrokerConfigurer {


    private final StompHandler stompHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp").setAllowedOriginPatterns("*")
                .withSockJS();
        registry.addEndpoint("/ws-stomp").setAllowedOriginPatterns("*");
    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler); // 핸들러 등록
    }

}
