package webrtc.chatservice.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.service.user.UserService;

@SpringBootTest
@Transactional
public class ChannelTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void clearUserCache() {
        userService.redisDataEvict();
    }


    @BeforeEach
    public void createUser() {
        User user1 = new User("user", "user", "email");
        userRepository.saveUser(user1);

    }

    @Test
    public void constructorChannel() {
        //given
        Channel channel = new Channel("TestChannel", false);

        //when

        //then
        Assertions.assertThat(channel.getChannelName()).isEqualTo("TestChannel");
    }

    @Test
    public void addChannelUser() {
        //given
        Channel channel = new Channel("TestChannel", false);
        User user = userRepository.findUserByEmail("email");
        ChannelUser channelUser = new ChannelUser(user, channel);

        //when
        channel.enterChannelUser(channelUser);

        //then
        Assertions.assertThat(channel.getCurrentParticipants()).isEqualTo(1);

        Assertions.assertThat(channelUser.getChannel()).isEqualTo(channel);
    }

    @Test
    public void addChannelHashTag() {

        // given
        Channel channel = new Channel("TestChannel", false);
        HashTag hashTag = new HashTag("TestTag");
        ChannelHashTag channelHashTag = new ChannelHashTag();
        channelHashTag.CreateChannelHashTag(channel, hashTag);

        // when
        channel.addChannelHashTag(channelHashTag);

        //then
        Assertions.assertThat(channel.getChannelHashTags().size()).isEqualTo(1);
        Assertions.assertThat(channelHashTag.getChannel()).isEqualTo(channel);
    }

    @Test
    public void minusCurrentParticipants() {
        //given
        Channel channel = new Channel("TestChannel", false);

        //when
//        channel.exitChannelUser();

        //then
        Assertions.assertThat(channel.getCurrentParticipants()).isEqualTo(0);
    }
}
