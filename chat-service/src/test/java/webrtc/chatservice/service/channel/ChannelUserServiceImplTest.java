package webrtc.chatservice.service.channel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.exception.ChannelUserException.NotExistChannelUserException;
import webrtc.chatservice.repository.channel.ChannelRepository;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.service.user.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class ChannelUserServiceImplTest {

    @Autowired
    private ChannelUserService channelUserService;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @BeforeEach
    public void clearUserCache() {
        userService.redisDataEvict();
    }


    @Test
    @DisplayName("ChannelUser 저장")
    public void saveChannelUser() {
        // given
        User user = new User("user", "user", "email");
        Channel channel = new Channel("testChannel");
        ChannelUser channelUser = new ChannelUser();
        channelUser.setChannel(channel);
        channelUser.setUser(user);

        // when
        channelRepository.save(channel);
        userRepository.saveUser(user);
        channelUserService.save(channelUser);
        ChannelUser findChannelUser = channelUserService.findOneChannelUser(channel.getId(), user.getId());

        // then
        assertThat(findChannelUser).isEqualTo(channelUser);
    }

    @Test
    @DisplayName("ChannelUser 삭제")
    public void deleteChannelUser() {
        // given
        User user = new User("user", "user", "email");
        Channel channel = new Channel("testChannel");
        ChannelUser channelUser = new ChannelUser();
        channelUser.setChannel(channel);
        channelUser.setUser(user);
        channelRepository.save(channel);
        userRepository.saveUser(user);
        channelUserService.save(channelUser);

        // when
        channelUserService.delete(channelUser);

        // then

    }

    @Test
    @DisplayName("ChannelUser 조회 성공")
    public void findOneChannelUserO() {
        // given
        User user = new User("user", "user", "email");
        Channel channel = new Channel("testChannel");
        ChannelUser channelUser = new ChannelUser();
        channelUser.setChannel(channel);
        channelUser.setUser(user);
        channelRepository.save(channel);
        userRepository.saveUser(user);
        channelUserService.save(channelUser);

        // when
        ChannelUser findChannelUser = channelUserService.findOneChannelUser(channel.getId(), user.getId());

        // then
        assertThat(findChannelUser).isEqualTo(channelUser);
    }

    @Test
    @DisplayName("ChannelUser 조회 실패")
    public void findOneChannelUserX() {
        // given
        User user = new User("user", "user", "email");
        Channel channel = new Channel("testChannel");
        ChannelUser channelUser = new ChannelUser();
        channelUser.setChannel(channel);
        channelUser.setUser(user);
        channelRepository.save(channel);
        userRepository.saveUser(user);
        channelUserService.save(channelUser);

        // when

        // then
        assertThrows(NotExistChannelUserException.class,
                ()-> channelUserService.findOneChannelUser("NotExistChannelId", user.getId()));
        assertThrows(NotExistChannelUserException.class,
                ()-> channelUserService.findOneChannelUser(channel.getId(), "NotExistUserId"));
    }
}
