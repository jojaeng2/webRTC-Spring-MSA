package webrtc.openvidu.repository.chat;

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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ChatLogRepositoryImplTest {

    @Autowired
    private ChatRepository chatLogRepository;
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
        List<ChatLog> chatLogs = chatLogRepository.findAllChatLogsByChannelId(findChannel.getId());

        // then
        assertThat(chatLogs.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("ChatLog 10개씩 불러오기")
    public void LoadIdxChatLog() {
        //given
        Channel findChannel = channelService.findChannelByHashName("tag1").get(0);
        for(int i=0; i<23; i++) {
            ChatLog chatLog = new ChatLog("testMessage" + i, "testUser2");
            chatLog.setChannel(findChannel);
        }

        //when
        List<ChatLog> chatLogs0 = chatLogRepository.findTenChatLogsByChannelId(findChannel.getId(), 0);
        List<ChatLog> chatLogs1 = chatLogRepository.findTenChatLogsByChannelId(findChannel.getId(), 1);

        //then
        assertThat(chatLogs0.size()).isEqualTo(10);
        assertThat(chatLogs1.size()).isEqualTo(10);
        for (ChatLog chatLog : chatLogs0) {
            System.out.println("chatLog = " + chatLog.getMessage());
            System.out.println("chatLog = " + chatLog.getSendTime());
        }
        System.out.println("123 = " + 123);
        for (ChatLog chatLog : chatLogs1) {
            System.out.println("chatLog = " + chatLog.getMessage());
            System.out.println("chatLog = " + chatLog.getSendTime());
        }
    }
}
