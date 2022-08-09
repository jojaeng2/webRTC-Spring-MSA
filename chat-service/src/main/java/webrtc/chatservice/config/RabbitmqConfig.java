package webrtc.chatservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {

    public static final String topicExchangeName = "chat-service";

    // queue Name
    public static final String chatTextQueueName = "chatText";

    public static final String chatEnterQueueName = "chatEnter";

    public static final String chatExitQueueName = "chatExit";

    public static final String chatTextRoutingKey = "chat.text";
    public static final String chatEnterRoutingKey = "chat.enter";
    public static final String chatExitRoutingKey = "chat.exit";

    @Bean
    public Queue chatTextQueue() {
        return new Queue(chatTextQueueName, true);
    }

    @Bean
    public Queue chatEnterQueue() {
        return new Queue(chatEnterQueueName, true);
    }

    @Bean
    public Queue chatExitQueue() {
        return new Queue(chatExitQueueName, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    public Binding chatTextBinding(Queue chatTextQueue, TopicExchange exchange) {
        return BindingBuilder.bind(chatTextQueue).to(exchange).with(chatTextRoutingKey);
    }

    @Bean
    public Binding chatEnterBinding(Queue chatEnterQueue, TopicExchange exchange) {
        return BindingBuilder.bind(chatEnterQueue).to(exchange).with(chatEnterRoutingKey);
    }

    @Bean
    public Binding chatExitBinding(Queue chatExitQueue, TopicExchange exchange) {
        return BindingBuilder.bind(chatExitQueue).to(exchange).with(chatExitRoutingKey);
    }


}