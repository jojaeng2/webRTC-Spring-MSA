package webrtc.openvidu.service.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ChatServiceImplTest {

    @Autowired
    private ChatService chatService;
}
