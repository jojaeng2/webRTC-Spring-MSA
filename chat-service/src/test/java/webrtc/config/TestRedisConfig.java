package webrtc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Configuration
public class TestRedisConfig {

    private RedisServer redisServer;
    public TestRedisConfig(@Value("${spring.redis.port}") int redisPort) {
        redisServer = new RedisServer(redisPort);
    }

    @PostConstruct
    public void startRedis() throws IOException {
        System.out.println("테스트 레디스 서버 ON");
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {

        redisServer.stop();
        System.out.println("테스트 레디스 서버 OFF");

    }
}
