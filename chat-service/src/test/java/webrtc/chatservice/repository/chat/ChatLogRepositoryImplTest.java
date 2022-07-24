package webrtc.chatservice.repository.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChatLog;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.repository.channel.ChannelRepository;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.service.channel.ChannelService;
import webrtc.chatservice.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ChannelType.VOIP;
import static webrtc.chatservice.enums.ClientMessageType.ENTER;

@SpringBootTest
public class ChatLogRepositoryImplTest {

    @Autowired
    private ChatLogRepository chatLogRepository;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    String nickname1 = "nickname1";
    String nickname2 = "nickname2";
    String password = "password";
    String email1 = "email1";
    String email2 = "email2";
    String channelName1 = "channelName1";
    String notExistChannelId = "null";
    String tag1 = "tag1";
    String tag2 = "tag2";
    String tag3 = "tag3";
    ChannelType text = TEXT;
    ChannelType voip = VOIP;
    @BeforeEach
    public void clearUserCache() {
        userService.redisDataEvict();
    }


    @BeforeEach
    public void 테스트용_채널생성() {
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, returnHashTags());
    }
    @BeforeEach
    public void 테스트용_유저생성() {
        User user1 = new User(nickname1, password, email1);
        userRepository.saveUser(user1);

        User user2 = new User(nickname2, password, email2);
        userRepository.saveUser(user2);
    }

    @Test
    @Transactional
    public void 채팅로그_저장_성공() {
        //given
        Channel channel =  channelRepository.findChannelByChannelName(channelName1);
        ChatLog chatLog = new ChatLog(ENTER, "testMessage", "testUser2", "email1");
        chatLog.setChannel(channel);

        //when
        chatLogRepository.save(chatLog);

        //then

    }

    @Test
    @Transactional
    public void 특정채널의_마지막_채팅로그_불러오기_성공() {
        // given
        Channel channel =  channelRepository.findChannelByChannelName(channelName1);
        
        for(Long i=0L; i<23L; i++) {
            ChatLog chatLog = new ChatLog(ENTER, "testMessage" + i, "testUser2", "email1");
            chatLog.setChatLogIdx(i);
            chatLog.setChannel(channel);
            chatLogRepository.save(chatLog);
        }

        // when
        List<ChatLog> findChatLogs = chatLogRepository.findLastChatLogsByChannelId(channel.getId());

        // then
        System.out.println("findChatLogs.size() = " + findChatLogs.size());
        assertThat(findChatLogs.get(0).getIdx()).isEqualTo(22L);
    }

    @Test
    @Transactional
    public void 채팅로그_20개씩_불러오기_성공() {
        // given
        Channel channel =  channelRepository.findChannelByChannelName(channelName1);
        for(Long i=0L; i<100L; i++) {
            ChatLog chatLog = new ChatLog(ENTER, "testMessage" + i, "testUser2", "email1");
            chatLog.setChatLogIdx(i);
            chatLog.setChannel(channel);
            chatLogRepository.save(chatLog);
        }

        // when
        List<ChatLog> chatLogs = chatLogRepository.findChatLogsByChannelId(channel.getId(), 30L);

        // then
        assertThat(chatLogs.size()).isEqualTo(20);
    }

    @Test
    @Transactional
    public void 로그20개이하_불러오기_성공() {
        // given
        Channel channel =  channelRepository.findChannelByChannelName(channelName1);
        for(Long i=0L; i<20L; i++) {
            ChatLog chatLog = new ChatLog(ENTER, "testMessage" + i, "testUser2", "email1");
            chatLog.setChatLogIdx(i);
            chatLog.setChannel(channel);
            chatLogRepository.save(chatLog);
        }

        // when
        List<ChatLog> chatLogs = chatLogRepository.findChatLogsByChannelId(channel.getId(), 15L);

        // then
        assertThat(chatLogs.size()).isEqualTo(14L);
    }

    public List<String> returnHashTags() {
        List<String> hashTags = new ArrayList<>();
        hashTags.add(tag1);
        hashTags.add(tag2);
        hashTags.add(tag3);
        return hashTags;
    }
    
}
