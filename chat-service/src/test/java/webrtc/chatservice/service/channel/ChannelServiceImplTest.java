package webrtc.chatservice.service.channel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.ChannelDto.ChannelResponse;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;
import webrtc.chatservice.exception.ChannelException.AlreadyExistChannelException;
import webrtc.chatservice.exception.ChannelException.ChannelParticipantsFullException;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.exception.PointException.InsufficientPointException;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class ChannelServiceImplTest {

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
    public void saveTestUser() {
        User user = new User("user", "user", "email");
        userRepository.saveUser(user);

        User user1 = new User("user1", "user1", "email1");
        userRepository.saveUser(user1);
    }

    @Test
    @DisplayName("채널 생성 O")
    public void createChannelO() {
        // given

        List<String> hashTags = new ArrayList<>();
        hashTags.add("testTag1");
        hashTags.add("testTag2");
        hashTags.add("testTag3");
        CreateChannelRequest request = new CreateChannelRequest("testChannel", hashTags);

        // when
        Channel createChannel = channelService.createChannel(request, "email");

        // then
        assertThat(createChannel.getChannelName()).isEqualTo("testChannel");
        assertThat(createChannel.getChannelHashTags().size()).isEqualTo(3);

    }

    @Test
    @DisplayName("채널 중복 발생")
    public void channelDuplicate() {
        // given
        List<String> hashTags = new ArrayList<>();
        hashTags.add("testTag1");
        hashTags.add("testTag2");
        hashTags.add("testTag3");
        CreateChannelRequest request = new CreateChannelRequest("testChannel", hashTags);

        // when
        channelService.createChannel(request, "email");

        // then
        assertThrows(AlreadyExistChannelException.class,
                () -> channelService.createChannel(request, "user"));
    }

    @Test
    @DisplayName("채널에 처음으로 입장")
    public void enterChannelFirstSuccess() {
        // given
        Channel createChannel = createChannelTemp();

        // when
        channelService.enterChannel(createChannel, "email1");

        // then
    }

    @Test
    @DisplayName("채널에 입장 + 채널 정보 확인")
    public void enterChannelAndChannelInfoSuccess() {
        // given
        Channel createChannel = createChannelTemp();

        // when
        channelService.enterChannel(createChannel, "email1");
        Channel findChannel = channelService.findOneChannelById(createChannel.getId());

        // then
        assertThat(findChannel.getCurrentParticipants()).isEqualTo(2);
    }


    @Test
    @DisplayName("인원 제한으로 채널 입장 실패")
    public void enterChannelFail() {
        // given
        Channel createChannel = createChannelTemp();

        // when
        for(int i=1; i<=14; i++) {
            User user = new User("user" + i, "user", "email" + i);
            userRepository.saveUser(user);
            channelService.enterChannel(createChannel, user.getEmail());
        }

        // then
        User user15 = new User("user15", "user", "email15");
        userRepository.saveUser(user15);
        assertThrows(ChannelParticipantsFullException.class,
                () -> channelService.enterChannel(createChannel, user15.getEmail()));
    }

    @Test
    @DisplayName("채널 퇴장")
    public void exitChannel() {
        // given
        Channel createChannel = createChannelTemp();
        User user = userService.findOneUserByEmail("email");

        // when
        channelService.exitChannel(createChannel.getId(), user);

        // then
        assertThat(createChannel.getChannelUsers().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("채널 삭제")
    public void deleteChannel() {
        // given
        Channel createChannel = createChannelTemp();

        // when
        channelService.deleteChannel(createChannel.getId());

        // then
    }

    @Test
    @DisplayName("모든 채널 불러오기")
    public void findAllChannel() {
        // given
        List<String> hashTags = new ArrayList<>();
        hashTags.add("testTag1");
        hashTags.add("testTag2");
        hashTags.add("testTag3");
        CreateChannelRequest request1 = new CreateChannelRequest("testChannel1", hashTags);
        CreateChannelRequest request2 = new CreateChannelRequest("testChannel2", hashTags);

        Channel createChannel1 = channelService.createChannel(request1, "email");
        Channel createChannel2 = channelService.createChannel(request2, "email");


        // when
        List<ChannelResponse> channels = channelService.findAnyChannel(0);

        // then
        assertThat(channels.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("ChannelId로 특정 채널 찾기 O")
    public void findOneChannelByIdO() {
        // given
        Channel createChannel = createChannelTemp();

        // when
        Channel findChannel = channelService.findOneChannelById(createChannel.getId());

        // then
        assertThat(createChannel).isEqualTo(findChannel);
    }

    @Test
    @DisplayName("ChannelId로 특정 채널 찾기 X")
    public void findOneChannelByIdX() {
        // given
        Channel createChannel = createChannelTemp();

        // when

        // then
        assertThrows(NotExistChannelException.class,
                () -> channelService.findOneChannelById("NotExistChannelId"));
    }

    @Test
    @DisplayName("HashTagName으로 채널 찾기")
    public void findChannelsByHashName() {
        //given
        List<String> hashTags1 = new ArrayList<>();
        hashTags1.add("testTag1");
        hashTags1.add("testTag2");
        hashTags1.add("testTag3");
        CreateChannelRequest request1 = new CreateChannelRequest("testChannel1", hashTags1);
        Channel createChannel1 = channelService.createChannel(request1, "email");

        List<String> hashTags2 = new ArrayList<>();
        hashTags2.add("testTag3");
        hashTags2.add("testTag4");
        hashTags2.add("testTag5");
        CreateChannelRequest request2 = new CreateChannelRequest("testChannel2", hashTags2);
        Channel createChannel2 = channelService.createChannel(request2, "email");


        //when
        List<Channel> findChannels1 = channelService.findChannelByHashName("testTag3");
        List<Channel> findChannels2 = channelService.findChannelByHashName("testTag2");
        List<Channel> findChannels3 = channelService.findChannelByHashName("testTag4");


        //then
        assertThat(findChannels1.size()).isEqualTo(2);
        assertThat(findChannels2.size()).isEqualTo(1);
        assertThat(findChannels3.size()).isEqualTo(1);
    }


    @Test
    @DisplayName("Extension ChannelTTL 실패 by NotExistChannelException")
    public void extensionChannelTTLFailByNotExistChannelException() {
        // given

        // when

        // then

        assertThrows(NotExistChannelException.class,
                () -> channelService.extensionChannelTTL("NotExistChannelId", "email", 100L));
    }

    @Test
    @DisplayName("Extension ChannelTTL 실패 by InsufficientPointException")
    public void extensionChannelTTLFailByInsufficientPointException() {
        // given
        Channel channel = createChannelTemp();

        // when

        // then

        assertThrows(InsufficientPointException.class,
                () -> channelService.extensionChannelTTL(channel.getId(), "email", 1000000L));
    }

    private Channel createChannelTemp() {
        List<String> hashTags = new ArrayList<>();
        hashTags.add("testTag1");
        hashTags.add("testTag2");
        hashTags.add("testTag3");
        CreateChannelRequest request = new CreateChannelRequest("testChannel", hashTags);
        return channelService.createChannel(request, "email");
    }
}
