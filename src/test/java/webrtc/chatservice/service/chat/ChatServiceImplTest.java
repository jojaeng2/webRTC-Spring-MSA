package webrtc.chatservice.service.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.repository.channel.ChannelRepository;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.service.channel.ChannelService;
import webrtc.chatservice.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ChatServiceImplTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChannelService channelService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;
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

        CreateChannelRequest request = new CreateChannelRequest("testChannel", hashTags, "chat");
        User user = new User("testUser", "testUser", "testEmail");
        userRepository.saveUser(user);
        channelService.createChannel(request, "testEmail");
    }

    @Test
    @DisplayName("새로운 chatLog 와 Idx 저장 성공")
    public void saveNewChatLogWithIdx() {
        // given
        Channel channel = channelRepository.findChannelsByChannelName("testChannel").get(0);

        // when
        Long chatId = chatService.saveChatLog(ClientMessageType.ENTER, "testMessage", "testUser", channel, "testEmail");

        // then
        assertThat(chatId).isEqualTo(2L);
    }

    @Test
    @DisplayName("기존의 chatLog에 chatLog 추가")
    public void saveChatLogInAlreadyChatLog() {
        // given
        Channel channel = channelRepository.findChannelsByChannelName("testChannel").get(0);
        for(Long i=1L; i<20L; i++) {
            chatService.saveChatLog(ClientMessageType.ENTER, "testMessage", "testUser", channel, "testEmail");
        }

        // when
        Long chatId = chatService.saveChatLog(ClientMessageType.ENTER, "testMessage", "testUser", channel, "testEmail");

        // then
        assertThat(chatId).isEqualTo(21L);
    }
}
