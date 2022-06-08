package webrtc.openvidu.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.repository.user.UserRepository;

@SpringBootTest
@Transactional
public class ChannelTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void createUser() {
        User user1 = new User("user", "user", "email");
        userRepository.saveUser(user1);

    }

    @Test
    public void constructorChannel() {
        //given
        Channel channel = new Channel("TestChannel");

        //when

        //then
        Assertions.assertThat(channel.getChannelName()).isEqualTo("TestChannel");
    }

    @Test
    public void addChannelUser() {
        //given
        Channel channel = new Channel("TestChannel");
        User user = userRepository.findUsersByEmail("email").get(0);
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
        Channel channel = new Channel("TestChannel");
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
        Channel channel = new Channel("TestChannel");

        //when
//        channel.exitChannelUser();

        //then
        Assertions.assertThat(channel.getCurrentParticipants()).isEqualTo(0);
    }
}
