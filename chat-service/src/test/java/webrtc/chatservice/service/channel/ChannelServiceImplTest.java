package webrtc.chatservice.service.channel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChatLog;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.ChannelException.AlreadyExistChannelException;
import webrtc.chatservice.repository.channel.ChannelRepository;
import webrtc.chatservice.repository.chat.ChatLogRepository;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.service.chat.ChatService;
import webrtc.chatservice.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ChannelType.VOIP;
import static webrtc.chatservice.enums.ClientMessageType.CHAT;
import static webrtc.chatservice.enums.ClientMessageType.CREATE;

@SpringBootTest
public class ChannelServiceImplTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;
    @Autowired
    private ChannelService channelService;



    String nickname1 = "nickname1";
    String nickname2 = "nickname2";
    String password = "password";
    String email1 = "email1";
    String email2 = "email2";
    String channelName1 = "channelName1";
    String channelName2 = "channelName2";
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
    public void 테스트용_유저생성() {
        User user1 = new User(nickname1, password, email1);
        userRepository.saveUser(user1);

        User user2 = new User(nickname2, password, email2);
        userRepository.saveUser(user2);
    }

    @BeforeEach
    public void 테스트용_채널생성() {
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, returnHashTags());
    }

    @Test
    @Transactional
    public void 채널생성_성공() {
        // given
        CreateChannelRequest request = new CreateChannelRequest(channelName2, returnHashTags(), TEXT);

        // when
        Channel createChannel = channelService.createChannel(request, email1);

        // then
        assertThat(createChannel.getChannelName()).isEqualTo(channelName2);
        assertThat(createChannel.getChannelHashTags().size()).isEqualTo(3);

    }

    @Test
    @Transactional
    public void 채널이름중복으로_채널생성_실패() {
        // given
        CreateChannelRequest request = new CreateChannelRequest(channelName1, returnHashTags(), TEXT);

        // when

        // then
        assertThrows(AlreadyExistChannelException.class,
                () -> {
                    channelService.createChannel(request, email1);
                });
    }


    @Test
    @Transactional
    public void 채널_첫번째메시지_반환성공() {
        // given
        CreateChannelRequest request = new CreateChannelRequest(channelName2, returnHashTags(), TEXT);
        Channel channel = channelService.createChannel(request, email1);

        // when
        ChatLog chatLog = chatService.findLastChatLogsByChannelId(channel.getId());

        // then
        assertThat(chatLog.getIdx()).isEqualTo(1L);
        assertThat(chatLog.getType()).isEqualTo(CREATE);
    }

    @Test
    @Transactional
    public void 채널_N번째메시지_반환성공() {
        // given
        int testCase = 10;
        CreateChannelRequest request = new CreateChannelRequest(channelName2, returnHashTags(), TEXT);
        Channel channel = channelService.createChannel(request, email1);
        for(int i=1; i<=testCase; i++) {
            chatService.saveChatLog(CHAT, i + " message", nickname1, channel, email1);
        }

        // when
        ChatLog chatLog = chatService.findLastChatLogsByChannelId(channel.getId());

        // then
        assertThat(chatLog.getIdx()).isEqualTo(testCase + 1L);
        assertThat(chatLog.getMessage()).isEqualTo(testCase + " message");
    }

//    @Test
//    @DisplayName("채널에 입장 + 채널 정보 확인")
//    public void enterChannelAndChannelInfoSuccess() {
//        // given
//        Channel createChannel = createChannelTemp();
//
//        // when
//        channelService.enterChannel(createChannel, "email1");
//        Channel findChannel = channelService.findOneChannelById(createChannel.getId());
//
//        // then
//        assertThat(findChannel.getCurrentParticipants()).isEqualTo(2);
//    }
//
//
//    @Test
//    @DisplayName("인원 제한으로 채널 입장 실패")
//    public void enterChannelFail() {
//        // given
//        Channel createChannel = createChannelTemp();
//
//        // when
//        for(int i=1; i<=14; i++) {
//            User user = new User("user" + i, "user", "email" + i);
//            userRepository.saveUser(user);
//            channelService.enterChannel(createChannel, user.getEmail());
//        }
//
//        // then
//        User user15 = new User("user15", "user", "email15");
//        userRepository.saveUser(user15);
//        assertThrows(ChannelParticipantsFullException.class,
//                () -> channelService.enterChannel(createChannel, user15.getEmail()));
//    }
//
//    @Test
//    @DisplayName("채널 퇴장")
//    public void exitChannel() {
//        // given
//        Channel createChannel = createChannelTemp();
//        User user = userService.findOneUserByEmail("email");
//
//        // when
//        channelService.exitChannel(createChannel.getId(), user);
//
//        // then
//        assertThat(createChannel.getChannelUsers().size()).isEqualTo(0);
//    }
//
//    @Test
//    @DisplayName("채널 삭제")
//    public void deleteChannel() {
//        // given
//        Channel createChannel = createChannelTemp();
//
//        // when
//        channelService.deleteChannel(createChannel.getId());
//
//        // then
//    }
//
//    @Test
//    @DisplayName("모든 채널 불러오기")
//    public void findAllChannel() {
//        // given
//        List<String> hashTags = new ArrayList<>();
//        hashTags.add("testTag1");
//        hashTags.add("testTag2");
//        hashTags.add("testTag3");
//        CreateChannelRequest request1 = new CreateChannelRequest("testChannel1", hashTags, "chat");
//        CreateChannelRequest request2 = new CreateChannelRequest("testChannel2", hashTags, "chat");
//
//        Channel createChannel1 = channelService.createChannel(request1, "email");
//        Channel createChannel2 = channelService.createChannel(request2, "email");
//
//
//        // when
//        List<ChannelResponse> channels = channelService.findAnyChannel(0);
//
//        // then
//        assertThat(channels.size()).isEqualTo(2);
//    }
//
//    @Test
//    @DisplayName("ChannelId로 특정 채널 찾기 O")
//    public void findOneChannelByIdO() {
//        // given
//        Channel createChannel = createChannelTemp();
//
//        // when
//        Channel findChannel = channelService.findOneChannelById(createChannel.getId());
//
//        // then
//        assertThat(createChannel).isEqualTo(findChannel);
//    }
//
//    @Test
//    @DisplayName("ChannelId로 특정 채널 찾기 X")
//    public void findOneChannelByIdX() {
//        // given
//        Channel createChannel = createChannelTemp();
//
//        // when
//
//        // then
//        assertThrows(NotExistChannelException.class,
//                () -> channelService.findOneChannelById("NotExistChannelId"));
//    }
//
//    @Test
//    @DisplayName("HashTagName으로 채널 찾기")
//    public void findChannelsByHashName() {
//        //given
//        List<String> hashTags1 = new ArrayList<>();
//        hashTags1.add("testTag1");
//        hashTags1.add("testTag2");
//        hashTags1.add("testTag3");
//        CreateChannelRequest request1 = new CreateChannelRequest("testChannel1", hashTags1, "chat");
//        Channel createChannel1 = channelService.createChannel(request1, "email");
//
//        List<String> hashTags2 = new ArrayList<>();
//        hashTags2.add("testTag3");
//        hashTags2.add("testTag4");
//        hashTags2.add("testTag5");
//        CreateChannelRequest request2 = new CreateChannelRequest("testChannel2", hashTags2, "chat");
//        Channel createChannel2 = channelService.createChannel(request2, "email");
//
//
//        //when
//        List<Channel> findChannels1 = channelService.findChannelByHashName("testTag3");
//        List<Channel> findChannels2 = channelService.findChannelByHashName("testTag2");
//        List<Channel> findChannels3 = channelService.findChannelByHashName("testTag4");
//
//
//        //then
//        assertThat(findChannels1.size()).isEqualTo(2);
//        assertThat(findChannels2.size()).isEqualTo(1);
//        assertThat(findChannels3.size()).isEqualTo(1);
//    }
//
//
//    @Test
//    @DisplayName("Extension ChannelTTL 실패 by NotExistChannelException")
//    public void extensionChannelTTLFailByNotExistChannelException() {
//        // given
//
//        // when
//
//        // then
//
//        assertThrows(NotExistChannelException.class,
//                () -> channelService.extensionChannelTTL("NotExistChannelId", "email", 100L));
//    }
//
//    @Test
//    @DisplayName("Extension ChannelTTL 실패 by InsufficientPointException")
//    public void extensionChannelTTLFailByInsufficientPointException() {
//        // given
//        Channel channel = createChannelTemp();
//
//        // when
//
//        // then
//
//        assertThrows(InsufficientPointException.class,
//                () -> channelService.extensionChannelTTL(channel.getId(), "email", 1000000L));
//    }

    public List<String> returnHashTags() {
        List<String> hashTags = new ArrayList<>();
        hashTags.add(tag1);
        hashTags.add(tag2);
        hashTags.add(tag3);
        return hashTags;
    }
}
