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
import static webrtc.openvidu.enums.ClientMessageType.ENTER;

@SpringBootTest
@Transactional
public class ChatLogRepositoryImpl2Test {

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
        ChatLog chatLog = new ChatLog(ENTER, "testMessage", "testUser2");
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
        ChatLog chatLog1 = new ChatLog(ENTER, "testMessage1", "testUser2");
        chatLog1.setChannel(findChannel);
        chatLogRepository.save(chatLog1);
        ChatLog chatLog2 = new ChatLog(ENTER, "testMessage2", "testUser2");
        chatLog2.setChannel(findChannel);
        chatLogRepository.save(chatLog2);

        // when
        List<ChatLog> chatLogs = chatLogRepository.findAllChatLogsByChannelId(findChannel.getId());

        // then
        assertThat(chatLogs.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("ChannelId를 기준으로 마지막 ChatLog 불러오기 성공")
    public void LastChatLogLoadByIdxSuccess() {
        // given
        Channel findChannel = channelService.findChannelByHashName("tag1").get(0);
        for(Long i=0L; i<23L; i++) {
            ChatLog chatLog = new ChatLog(ENTER, "testMessage" + i, "testUser2");
            chatLog.setChatLogIdx(i);
            chatLog.setChannel(findChannel);
            chatLogRepository.save(chatLog);
        }

        // when
        List<ChatLog> findChatLogs = chatLogRepository.findLastChatLogsByChannelId(findChannel.getId());

        // then
        assertThat(findChatLogs.get(0).getIdx()).isEqualTo(22L);
    }

    @Test
    @DisplayName("ChannelId를 기준으로 마지막 ChatLog 불러오기 실패")
    public void LastChatLogLoadByIdxFail() {
        // given
        Channel findChannel = channelService.findChannelByHashName("tag1").get(0);

        // when
        List<ChatLog> findChatLogs = chatLogRepository.findLastChatLogsByChannelId(findChannel.getId());

        // then
        assertThat(findChatLogs.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("channelId와 idx로 [idx-21 ~ idx-1] 로그 20개씩 불러오기")
    public void LoadChatLogByChannelIdAndIdxSuccess() {
        // given
        Channel findChannel = channelService.findChannelByHashName("tag1").get(0);
        for(Long i=0L; i<100L; i++) {
            ChatLog chatLog = new ChatLog(ENTER, "testMessage" + i, "testUser2");
            chatLog.setChatLogIdx(i);
            chatLog.setChannel(findChannel);
            chatLogRepository.save(chatLog);
        }

        // when
        List<ChatLog> chatLogs = chatLogRepository.findChatLogsByChannelId(findChannel.getId(), 30L);

        // then
        for (ChatLog chatLog : chatLogs) {
            System.out.println("chatLog = " + chatLog.getIdx());
        }
        assertThat(chatLogs.size()).isEqualTo(20);
    }

    @Test
    @DisplayName("channelId와 idx로 [idx-21 ~ 0] 로그 20개 이하씩 불러오기")
    public void LoadChatLogByChannelIdAndIdxFromZeroSuccess() {
        // given
        Channel findChannel = channelService.findChannelByHashName("tag1").get(0);
        for(Long i=0L; i<20L; i++) {
            ChatLog chatLog = new ChatLog(ENTER, "testMessage" + i, "testUser2");
            chatLog.setChatLogIdx(i);
            chatLog.setChannel(findChannel);
            chatLogRepository.save(chatLog);
        }

        // when
        List<ChatLog> chatLogs = chatLogRepository.findChatLogsByChannelId(findChannel.getId(), 15L);

        // then
        for (ChatLog chatLog : chatLogs) {
            System.out.println("chatLog = " + chatLog.getIdx());
        }
        assertThat(chatLogs.size()).isEqualTo(14L);
    }
}