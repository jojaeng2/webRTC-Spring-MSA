package webrtc.openvidu.service.channel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.dto.ChannelDto;
import webrtc.openvidu.dto.ChannelDto.ChannelResponse;
import webrtc.openvidu.dto.ChannelDto.CreateChannelRequest;
import webrtc.openvidu.exception.ChannelException.AlreadyExistChannelException;
import webrtc.openvidu.exception.ChannelException.ChannelParticipantsFullException;
import webrtc.openvidu.exception.ChannelException.NotExistChannelException;
import webrtc.openvidu.repository.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class ChannelServiceImplTest {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void saveTestUser() {
        User user = new User("user", "user");
        userRepository.saveUser(user);

        User user1 = new User("user1", "user1");
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
        Channel createChannel = channelService.createChannel(request, "user");

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
        channelService.createChannel(request, "user");

        // then
        Assertions.assertThrows(AlreadyExistChannelException.class,
                () -> channelService.createChannel(request, "user"));
    }

    @Test
    @DisplayName("채널에 처음으로 입장")
    public void enterChannelFirstSuccess() {
        // given
        List<String> hashTags = new ArrayList<>();
        hashTags.add("testTag1");
        hashTags.add("testTag2");
        hashTags.add("testTag3");
        CreateChannelRequest request = new CreateChannelRequest("testChannel", hashTags);
        Channel createChannel = channelService.createChannel(request, "user");

        // when
        channelService.enterChannel(createChannel, "user1");

        // then
    }

    @Test
    @DisplayName("채널에 입장 + 채널 정보 확인")
    public void enterChannelAndChannelInfoSuccess() {
        // given
        List<String> hashTags = new ArrayList<>();
        hashTags.add("testTag1");
        hashTags.add("testTag2");
        hashTags.add("testTag3");
        CreateChannelRequest request = new CreateChannelRequest("testChannel", hashTags);
        Channel createChannel = channelService.createChannel(request, "user");

        // when
        channelService.enterChannel(createChannel, "user1");
        Channel findChannel = channelService.findOneChannelById(createChannel.getId());

        // then
        assertThat(findChannel.getCurrentParticipants()).isEqualTo(2);
    }


    @Test
    @DisplayName("인원 제한으로 채널 입장 실패")
    public void enterChannelFail() {
        // given
        List<String> hashTags = new ArrayList<>();
        hashTags.add("testTag1");
        hashTags.add("testTag2");
        hashTags.add("testTag3");
        CreateChannelRequest request = new CreateChannelRequest("testChannel", hashTags);
        Channel createChannel = channelService.createChannel(request, "user");

        // when
        for(int i=1; i<=14; i++) {
            User user = new User("user" + i, "user");
            userRepository.saveUser(user);
            channelService.enterChannel(createChannel, user.getNickname());
        }

        // then
        User user15 = new User("user15", "user");
        userRepository.saveUser(user15);
        Assertions.assertThrows(ChannelParticipantsFullException.class,
                () -> channelService.enterChannel(createChannel, user15.getNickname()));
    }

    @Test
    @DisplayName("채널 퇴장")
    public void exitChannel() {
        // given
        List<String> hashTags = new ArrayList<>();
        hashTags.add("testTag1");
        hashTags.add("testTag2");
        hashTags.add("testTag3");
        CreateChannelRequest request = new CreateChannelRequest("testChannel", hashTags);
        Channel createChannel = channelService.createChannel(request, "user");

        // when
        channelService.exitChannel(createChannel.getId(), "user");

        // then
        assertThat(createChannel.getChannelUsers().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("채널 삭제")
    public void deleteChannel() {
        // given
        List<String> hashTags = new ArrayList<>();
        hashTags.add("testTag1");
        hashTags.add("testTag2");
        hashTags.add("testTag3");
        CreateChannelRequest request = new CreateChannelRequest("testChannel", hashTags);
        Channel createChannel = channelService.createChannel(request, "user");

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

        Channel createChannel1 = channelService.createChannel(request1, "user");
        Channel createChannel2 = channelService.createChannel(request2, "user");


        // when
        List<ChannelResponse> channels = channelService.findAnyChannel(0);

        // then
        assertThat(channels.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("ChannelId로 특정 채널 찾기 O")
    public void findOneChannelByIdO() {
        // given
        List<String> hashTags = new ArrayList<>();
        hashTags.add("testTag1");
        hashTags.add("testTag2");
        hashTags.add("testTag3");
        CreateChannelRequest request = new CreateChannelRequest("testChannel", hashTags);
        Channel createChannel = channelService.createChannel(request, "user");

        // when
        Channel findChannel = channelService.findOneChannelById(createChannel.getId());

        // then
        assertThat(createChannel).isEqualTo(findChannel);
    }

    @Test
    @DisplayName("ChannelId로 특정 채널 찾기 X")
    public void findOneChannelByIdX() {
        // given
        List<String> hashTags = new ArrayList<>();
        hashTags.add("testTag1");
        hashTags.add("testTag2");
        hashTags.add("testTag3");
        CreateChannelRequest request = new CreateChannelRequest("testChannel", hashTags);
        Channel createChannel = channelService.createChannel(request, "user");

        // when

        // then
        Assertions.assertThrows(NotExistChannelException.class,
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
        Channel createChannel1 = channelService.createChannel(request1, "user");

        List<String> hashTags2 = new ArrayList<>();
        hashTags2.add("testTag3");
        hashTags2.add("testTag4");
        hashTags2.add("testTag5");
        CreateChannelRequest request2 = new CreateChannelRequest("testChannel2", hashTags2);
        Channel createChannel2 = channelService.createChannel(request2, "user");


        //when
        List<Channel> findChannels1 = channelService.findChannelByHashName("testTag3");
        List<Channel> findChannels2 = channelService.findChannelByHashName("testTag2");
        List<Channel> findChannels3 = channelService.findChannelByHashName("testTag4");


        //then
        assertThat(findChannels1.size()).isEqualTo(2);
        assertThat(findChannels2.size()).isEqualTo(1);
        assertThat(findChannels3.size()).isEqualTo(1);

    }
}
