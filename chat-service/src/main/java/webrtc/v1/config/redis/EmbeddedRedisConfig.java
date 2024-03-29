package webrtc.v1.config.redis;

import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

@Profile("local") // profile이 local일때만 활성화
@Configuration
public class EmbeddedRedisConfig {

  @Value("${spring.redis.port:6379}")
  private int redisPort;

  private RedisServer redisServer;

  @PostConstruct
  public void redisServer() throws IOException {
    redisServer = new RedisServer(redisPort);
    redisServer.start();
  }

  @PreDestroy
  public void stopRedis() {
    if (redisServer != null) {
      redisServer.stop();
    }
  }
}