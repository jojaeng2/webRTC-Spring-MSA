package webrtc.voiceservice.repository.session;

import io.openvidu.java.client.OpenVidu;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import webrtc.voiceservice.config.RedisConfig;
import webrtc.voiceservice.config.TestRedisConfig;

@RunWith(SpringRunner.class)
@Import({
        RedisConfig.class,
        TestRedisConfig.class,
        OpenViduSessionRepositoryImpl.class
}) // DataRedisTest로 찾지 못하는 Config 클래스를 import
@DataRedisTest
public class OpenViduSessionRepositoryImplTest {

    @Autowired
    private OpenViduSessionRepository openViduSessionRepository;

    @Test
    public void 세션생성성공() throws Exception {
        // given

        // when

        // then

    }

}
