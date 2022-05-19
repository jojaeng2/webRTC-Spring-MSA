package webrtc.openvidu.repository.chat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.domain.ChatLog;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.dto.ChannelDto.CreateChannelRequest;
import webrtc.openvidu.repository.user.UserRepository;
import webrtc.openvidu.service.channel.ChannelService;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
public class ChatLogRepositoryImplTest {

    @Autowired
    private ChatLogRepository chatLogRepository;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void saveChannel() {
        List<String> hashTags = new ArrayList<>();
        hashTags.add("tag1");
        hashTags.add("tag2");
        hashTags.add("tag2");

        CreateChannelRequest request = new CreateChannelRequest("testChannel", hashTags);
        User user = new User("testUser", "testUser");
        userRepository.saveUser(user);
        channelService.createChannel(request, "testUser");
    }

    @Test
    @DisplayName("chatLog 저장")
    public void saveChatLog() {
        //given
        Channel findChannel = channelService.findChannelByHashName("tag1").get(0);
        ChatLog chatLog = new ChatLog("testMessage", "testUser2");
        chatLog.setChannel(findChannel);

        //when
        chatLogRepository.save(chatLog);

        //then

    }
    
    @Test
    @DisplayName("모든 ChatLog 불러오기")
    public void LoadAllChatLog() {
        // given
        Channel findChannel = channelService.findChannelByHashName("tag1").get(0);
        ChatLog chatLog1 = new ChatLog("testMessage1", "testUser2");
        chatLog1.setChannel(findChannel);

        ChatLog chatLog2 = new ChatLog("testMessage2", "testUser2");
        chatLog2.setChannel(findChannel);

        // when
        List<ChatLog> chatLogs = chatLogRepository.findAllChatLogsByChannelId();

        // then
        Assertions.assertThat(chatLogs.size()).isEqualTo(2);
    }
}
