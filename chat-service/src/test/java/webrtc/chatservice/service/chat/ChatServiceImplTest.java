package webrtc.chatservice.service.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChatLog;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.repository.channel.ChannelRepository;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ChannelType.VOIP;

@SpringBootTest
public class ChatServiceImplTest {

    @Autowired
    private ChatService chatService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;
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
    public void saveChannel() {
        User user1 = new User(nickname1, password, email1);
        userRepository.saveUser(user1);

        User user2 = new User(nickname2, password, email2);
        userRepository.saveUser(user2);

        Channel channel = new Channel(channelName1, text);
        Channel createdChannel = channelRepository.createChannel(channel, returnHashTags());
    }

    @Test
    @Transactional
    public void 첫번째_채팅로그_저장성공() {
        // given
        Channel channel = channelRepository.findChannelByChannelName(channelName1);

        // when
        Long chatId = chatService.saveChatLog(ClientMessageType.ENTER, "testMessage", nickname1, channel, email1);

        // then
        assertThat(chatId).isEqualTo(1L);
    }

    @Test
    @Transactional
    public void N번째_채팅로그_저장성공() {
        // given
        Long testcase = 20L;
        Channel channel = channelRepository.findChannelByChannelName(channelName1);

        for(Long i=0L; i<testcase; i++) {
            chatService.saveChatLog(ClientMessageType.ENTER, "testMessage", nickname1, channel, email1);
        }

        // when
        Long chatId = chatService.saveChatLog(ClientMessageType.ENTER, "testMessage", nickname1, channel, email1);

        // then
        assertThat(chatId).isEqualTo(21L);
    }

    @Test
    @Transactional
    public void 채팅로그_20개미만_불러오기_성공() {
        // given
        Long testcase = 10L;
        Channel channel = channelRepository.findChannelByChannelName(channelName1);

        for(Long i=0L; i<testcase; i++) {
            chatService.saveChatLog(ClientMessageType.ENTER, "testMessage", nickname1, channel, email1);
        }

        // when
        List<ChatLog> chatLogs = chatService.findChatLogsByIndex(channel.getId(), testcase+1L);

        // then
        assertThat(Long.valueOf(chatLogs.size())).isEqualTo(testcase);
    }

    @Test
    @Transactional
    public void 채팅로그_20개초과_불러오기_성공() {
        // given
        Long testcase = 30L;
        Channel channel = channelRepository.findChannelByChannelName(channelName1);

        for(Long i=0L; i<testcase; i++) {
            chatService.saveChatLog(ClientMessageType.ENTER, "testMessage", nickname1, channel, email1);
        }

        // when
        List<ChatLog> chatLogs = chatService.findChatLogsByIndex(channel.getId(), testcase+1L);

        // then
        assertThat(Long.valueOf(chatLogs.size())).isEqualTo(20L);
    }


    public List<String> returnHashTags() {
        List<String> hashTags = new ArrayList<>();
        hashTags.add(tag1);
        hashTags.add(tag2);
        hashTags.add(tag3);
        return hashTags;
    }
}
