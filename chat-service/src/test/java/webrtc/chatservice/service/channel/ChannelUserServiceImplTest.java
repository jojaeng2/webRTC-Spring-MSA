package webrtc.chatservice.service.channel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.ChannelUserException.NotExistChannelUserException;
import webrtc.chatservice.repository.channel.ChannelRepository;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ChannelType.VOIP;

@SpringBootTest
public class ChannelUserServiceImplTest {

    @Autowired
    private ChannelUserService channelUserService;
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
    public void 채널유저_조회성공() {
        // given
        User user = userRepository.findUserByEmail(email1);
        Channel channel = channelRepository.findChannelByChannelName(channelName1);

        ChannelUser channelUser = new ChannelUser(user, channel);
        channelRepository.enterChannelUserInChannel(channel, channelUser);

        // when
        ChannelUser findChannelUser = channelUserService.findOneChannelUser(channel.getId(), user.getId());

        // then
        assertThat(findChannelUser).isEqualTo(channelUser);
    }

    @Test
    @Transactional
    public void 채널유저_조회실패() {
        // given
        User user = userRepository.findUserByEmail(email1);
        Channel channel = channelRepository.findChannelByChannelName(channelName1);

        // when

        // then
        Assertions.assertThrows(NotExistChannelUserException.class, ()->{
            channelUserService.findOneChannelUser(channel.getId(), user.getId());
        });
    }

    public List<String> returnHashTags() {
        List<String> hashTags = new ArrayList<>();
        hashTags.add(tag1);
        hashTags.add(tag2);
        hashTags.add(tag3);
        return hashTags;
    }
}
