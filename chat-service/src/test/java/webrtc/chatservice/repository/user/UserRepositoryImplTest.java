package webrtc.chatservice.repository.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.channel.ChannelRepository;
import webrtc.chatservice.repository.channel.ChannelUserRepository;
import webrtc.chatservice.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ChannelType.VOIP;

@SpringBootTest
public class UserRepositoryImplTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private ChannelUserRepository channelUserRepository;
    @Autowired
    private UserService userService;

    @BeforeEach
    public void clearUserCache() {
        userService.redisDataEvict();
    }

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

    @Test
    @Transactional
    public void 유저저장_성공() {
        // given
        User user = new User("user", "user", "email");

        // when
        userRepository.saveUser(user);

        // then

    }

    @Test
    @Transactional
    public void 유저저장_성공_AND_이메일로조회_성공() {
        //given
        User user = new User(nickname1, password, email1);
        userRepository.saveUser(user);

        //when
        User findUser = userRepository.findUserByEmail(email1);

        //then
        assertThat(findUser).isEqualTo(user);
    }

    @Test
    @Transactional
    public void 유저저장_성공_AND_이메일로조회_실패() {
        //given
        User user = new User(nickname1, password, email1);
        userRepository.saveUser(user);

        //when


        //then
        assertThrows(NotExistUserException.class,
                () -> userRepository.findUserByEmail(email2));
    }

    @Test
    @Transactional
    public void 유저채널입장성공_AND_채널ID로조회_성공() {
        //given
        User user = new User(nickname1, password, email1);
        userRepository.saveUser(user);
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, new ArrayList<>());
        ChannelUser channelUser = new ChannelUser(user, channel);
        channelRepository.enterChannelUserInChannel(channel, channelUser);

        //when

        List<User> findUsers = userRepository.findUsersByChannelId(channel.getId());

        //then
        assertThat(findUsers.get(0)).isEqualTo(user);
    }

    @Test
    @Transactional
    public void 유저채널입장성공_AND_채널ID로조회_실패() {
        //given
        User user = new User(nickname1, password, email1);
        userRepository.saveUser(user);
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, new ArrayList<>());

        //when

        List<User> userList = userRepository.findUsersByChannelId("NotExistChannelId");

        //then
        assertThat(userList.isEmpty()).isEqualTo(true);
    }

}
