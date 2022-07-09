package webrtc.chatservice.repository.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChatLog;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.service.channel.ChannelService;
import webrtc.chatservice.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static webrtc.chatservice.enums.ClientMessageType.ENTER;

@SpringBootTest
@Transactional
public class ChatLogRepositoryImplTest {

    @Autowired
    private ChatLogRepository chatLogRepository;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void clearUserCache() {
        userService.redisDataEvict();
    }


    @BeforeEach
    public void saveChannel() {
        List<String> hashTags = new ArrayList<>();
        hashTags.add("tag1");
        hashTags.add("tag2");
        hashTags.add("tag2");

        CreateChannelRequest request = new CreateChannelRequest("testChannel", hashTags, false);
        User user = new User("testUser", "testUser", "email1");
        userRepository.saveUser(user);
        channelService.createChannel(request, "email1");
    }

    @Test
    @DisplayName("chatLog 저장")
    public void saveChatLog() {
        //given
        Channel findChannel = channelService.findChannelByHashName("tag1").get(0);
        ChatLog chatLog = new ChatLog(ENTER, "testMessage", "testUser2", "email1");
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
        ChatLog chatLog1 = new ChatLog(ENTER, "testMessage1", "testUser2", "email1");
        chatLog1.setChannel(findChannel);
        chatLogRepository.save(chatLog1);
        ChatLog chatLog2 = new ChatLog(ENTER, "testMessage2", "testUser2", "email1");
        chatLog2.setChannel(findChannel);
        chatLogRepository.save(chatLog2);

        // when
        List<ChatLog> chatLogs = chatLogRepository.findAllChatLogsByChannelId(findChannel.getId());

        // then
        assertThat(chatLogs.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("ChannelId를 기준으로 마지막 ChatLog 불러오기 성공")
    public void LastChatLogLoadByIdxSuccess() {
        // given
        Channel findChannel = channelService.findChannelByHashName("tag1").get(0);
        for(Long i=0L; i<23L; i++) {
            ChatLog chatLog = new ChatLog(ENTER, "testMessage" + i, "testUser2", "email1");
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
    @DisplayName("Channel  처음 생성했을때 chatLog 1개 정상적으로 생성")
    public void LastChatLogLoadByIdxFail() {
        // given
        Channel findChannel = channelService.findChannelByHashName("tag1").get(0);

        // when
        List<ChatLog> findChatLogs = chatLogRepository.findLastChatLogsByChannelId(findChannel.getId());

        // then
        assertThat(findChatLogs.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("channelId와 idx로 [idx-21 ~ idx-1] 로그 20개씩 불러오기")
    public void LoadChatLogByChannelIdAndIdxSuccess() {
        // given
        Channel findChannel = channelService.findChannelByHashName("tag1").get(0);
        for(Long i=0L; i<100L; i++) {
            ChatLog chatLog = new ChatLog(ENTER, "testMessage" + i, "testUser2", "email1");
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
            ChatLog chatLog = new ChatLog(ENTER, "testMessage" + i, "testUser2", "email1");
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
        assertThat(chatLogs.size()).isEqualTo(15L);
    }
}
