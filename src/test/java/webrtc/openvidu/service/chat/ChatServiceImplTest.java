package webrtc.openvidu.service.chat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.dto.ChannelDto;
import webrtc.openvidu.dto.ChannelDto.CreateChannelRequest;
import webrtc.openvidu.enums.ClientMessageType;
import webrtc.openvidu.repository.channel.ChannelRepository;
import webrtc.openvidu.repository.user.UserRepository;
import webrtc.openvidu.service.channel.ChannelService;

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
    @DisplayName("새로운 chatLog 와 Idx 저장 성공")
    public void saveNewChatLogWithIdx() {
        // given
        Channel channel = channelRepository.findChannelsByChannelName("testChannel").get(0);

        // when
        Long chatId = chatService.saveChatLog(ClientMessageType.ENTER, "testMessage", "testUser", channel);

        // then
        assertThat(chatId).isEqualTo(1L);
    }

    @Test
    @DisplayName("기존의 chatLog에 chatLog 추가")
    public void saveChatLogInAlreadyChatLog() {
        // given
        Channel channel = channelRepository.findChannelsByChannelName("testChannel").get(0);
        for(Long i=1L; i<20L; i++) {
            chatService.saveChatLog(ClientMessageType.ENTER, "testMessage", "testUser", channel);
        }

        // when
        Long chatId = chatService.saveChatLog(ClientMessageType.ENTER, "testMessage", "testUser", channel);

        // then
        assertThat(chatId).isEqualTo(20L);
    }
}
