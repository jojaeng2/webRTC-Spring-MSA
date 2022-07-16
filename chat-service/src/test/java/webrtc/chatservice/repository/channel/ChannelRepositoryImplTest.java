package webrtc.chatservice.repository.channel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.HashTag;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.repository.hashtag.HashTagRepository;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ChannelRepositoryImplTest {

    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelUserRepository channelUserRepository;
    @Autowired
    private HashTagRepository hashTagRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void clearUserCache() {
        userService.redisDataEvict();
    }


    @BeforeEach
    public void createUser() {
        User user1 = new User("user1", "user", "email1");
        userRepository.saveUser(user1);

        User user2 = new User("user2", "user", "email2");
        userRepository.saveUser(user2);
    }

    @Test
    @DisplayName("채널 생성")
    public void createChannel() {
        // given
        Channel channel = createChannelTemp();

        // when
        Channel findChannel = channelRepository.findChannelsByChannelName(channel.getChannelName()).get(0);

        // then
        assertThat(findChannel).isEqualTo(channel);
    }

    @Test
    @DisplayName("Any 채널 목록 불러오기")
    public void LoadAnyChannelsList() {
        // given
        List<String> hashTags = new ArrayList<>();
        hashTags.add("tag1");
        hashTags.add("tag2");
        hashTags.add("tag3");

        for(int i=0; i<25; i++) {
            Channel channel = new Channel("testChannel" + i, "chat");
            channelRepository.createChannel(channel, hashTags);
        }

        // when
        List<Channel> findChannels1 = channelRepository.findAnyChannel(0);
        List<Channel> findChannels2 = channelRepository.findAnyChannel(1);

        // then
        assertThat(findChannels1.size()).isEqualTo(20);
        assertThat(findChannels2.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("My 채널 목록 불러오기")
    public void LoadMyChannelsList() {
        // given
        List<String> hashTags = new ArrayList<>();
        hashTags.add("tag1");
        hashTags.add("tag2");
        hashTags.add("tag3");

        for(int i=0; i<7; i++) {
            Channel channel = new Channel("testChannel" + i, "chat");
            channelRepository.createChannel(channel, hashTags);
            User user = userRepository.findUserByEmail("email1");

            ChannelUser channelUser = new ChannelUser(user, channel);
            channel.enterChannelUser(channelUser);
            user.addChannelUser(channelUser);
            channelUserRepository.save(channelUser);
        }

        for(int i=0; i<5; i++) {
            Channel channel = new Channel("testChannel" + i, "chat");
            channelRepository.createChannel(channel, hashTags);
            User user = userRepository.findUserByEmail("email2");

            ChannelUser channelUser = new ChannelUser(user, channel);
            channel.enterChannelUser(channelUser);
            user.addChannelUser(channelUser);
            channelUserRepository.save(channelUser);
        }

        // when
        User user1 = userRepository.findUserByEmail("email1");
        User user2 = userRepository.findUserByEmail("email2");

        List<Channel> findChannels1 = channelRepository.findMyChannel(user1.getId(), 0);
        List<Channel> findChannels2 = channelRepository.findMyChannel(user2.getId(), 0);

        // then
        assertThat(findChannels1.size()).isEqualTo(7);
        assertThat(findChannels2.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("채널을 channelID로 찾기 성공")
    public void findChannelsByChannelIdSuccess() {
        // given
        Channel channel = createChannelTemp();

        // when
        Channel findChannel = channelRepository.findChannelsById(channel.getId()).get(0);

        // then
        assertThat(channel).isEqualTo(findChannel);
    }

    @Test
    @DisplayName("채널을 channelID로 찾기 실패")
    public void findChannelsByChannelIdFail() {
        // given
        Channel channel = createChannelTemp();

        // when
        List<Channel> findChannels = channelRepository.findChannelsById("NotExistId");

        // then
        assertThat(findChannels.size()).isEqualTo(0);

    }

    @Test
    @DisplayName("채널을 userId로 찾기 성공")
    public void findChannelsByUserIdSuccess() {
        // given
        Channel channel = createChannelTemp();

        User user1 = userRepository.findUserByEmail("email1");
        ChannelUser channelUser = new ChannelUser(user1, channel);
        channel.enterChannelUser(channelUser);
        user1.addChannelUser(channelUser);
        channelUserRepository.save(channelUser);

        // when
        List<Channel> findChannels = channelRepository.findChannelsByUserId(channel.getId(), user1.getId());

        // then
        assertThat(findChannels.get(0)).isEqualTo(channel);
    }

    @Test
    @DisplayName("채널을 userId로 찾기 실패")
    public void findChannelsByUserIdFail() {
        // given
        Channel channel = createChannelTemp();

        User user1 = userRepository.findUserByEmail("email1");
        ChannelUser channelUser = new ChannelUser();
        channel.enterChannelUser(channelUser);
        user1.addChannelUser(channelUser);
        channelUserRepository.save(channelUser);

        // when
        List<Channel> findChannels = channelRepository.findChannelsByUserId(channel.getId(), "NotExistUserId");

        // then
        assertThat(findChannels.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("채널을 hashName으로 찾기 성공")
    public void findChannelsByHashNameSuccess() {
        // given
        Channel channel = createChannelTemp();

        // when
        List<Channel> findChannels = channelRepository.findChannelsByHashName("tag1");

        // then
        assertThat(findChannels.get(0)).isEqualTo(channel);
    }

    @Test
    @DisplayName("채널을 hashName으로 찾기 실패")
    public void findChannelsByHashNameFail() {
        // given
        List<String> hashTags = new ArrayList<>();
        hashTags.add("tag1");
        hashTags.add("tag2");
        hashTags.add("tag3");
        Channel channel = new Channel("testChannel", "chat");
        channelRepository.createChannel(channel, hashTags);
        HashTag hashTag = new HashTag("tag4");
        hashTagRepository.save(hashTag);

        // when
        List<Channel> findChannels = channelRepository.findChannelsByHashName("tag4");

        // then
        assertThat(findChannels.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("채널을 channemName으로 찾기 성공")
    public void findChannelsByChannelNameSuccess() {
        // given
        Channel channel = createChannelTemp();

        // when
        List<Channel> findChannels = channelRepository.findChannelsByChannelName("testChannel");

        // then
        assertThat(findChannels.get(0)).isEqualTo(channel);
    }

    @Test
    @DisplayName("채널을 channemName으로 찾기 실패")
    public void findChannelsByChannelNameFail() {
        // given
        List<String> hashTags = new ArrayList<>();
        hashTags.add("tag1");
        hashTags.add("tag2");
        hashTags.add("tag3");
        Channel channel = new Channel("testChannel", "chat");
        channelRepository.createChannel(channel, hashTags);

        // when
        List<Channel> findChannels = channelRepository.findChannelsByChannelName("NotExistChannelName");

        // then
        assertThat(findChannels.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("채널의 TTL 연장 성공")
    public void extensionChannelTTLSuccess() {
        // given

        // when

        // then

    }

    public Channel createChannelTemp() {
        List<String> hashTags = new ArrayList<>();
        hashTags.add("tag1");
        hashTags.add("tag2");
        hashTags.add("tag3");
        Channel channel = new Channel("testChannel", "chat");
        channelRepository.createChannel(channel, hashTags);
        return channel;
    }


}
