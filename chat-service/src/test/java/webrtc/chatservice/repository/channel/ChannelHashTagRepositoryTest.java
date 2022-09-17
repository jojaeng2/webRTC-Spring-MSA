package webrtc.chatservice.repository.channel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
public class ChannelHashTagRepositoryTest {

    @Autowired
    private ChannelHashTagRepository channelHashTagRepository;

}
