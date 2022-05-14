package webrtc.openvidu.repository.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.domain.ChannelUser;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.repository.channel.ChannelRepository;
import webrtc.openvidu.repository.channel.ChannelUserRepository;

import java.util.List;

@SpringBootTest
@Transactional
public class UserRepositoryImplTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private ChannelUserRepository channelUserRepository;

    @Test
    @DisplayName("User 저장 O")
    public void saveUser() {
        // given
        User user = new User("user", "user");

        // when
        userRepository.saveUser(user);

        // then

    }

    @Test
    @DisplayName("User 저장 O && username 조회 O")
    public void user_saveO_findByNameO() {
        //given
        User user = new User("user", "user");

        //when
        userRepository.saveUser(user);
        List<User> findUsers = userRepository.findUsersByName(user.getNickname());

        //then
        Assertions.assertThat(findUsers.get(0)).isEqualTo(user);
    }

    @Test
    @DisplayName("User 저장 O && username 조회 X")
    public void user_saveO_findByNameX() {
        //given
        User user = new User("user", "user");

        //when
        userRepository.saveUser(user);
        List<User> findUsers = userRepository.findUsersByName("user1");


        //then
        Assertions.assertThat(findUsers.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("User 저장 O && channelId 조회 O")
    public void user_saveO_findByChannelIdO() {
        //given
        User user = new User("user", "user");
        Channel channel = new Channel("testChannel");
        ChannelUser channelUser = new ChannelUser();
        channelUser.setChannel(channel);
        channelUser.setUser(user);

        //when
        channelRepository.save(channel);
        userRepository.saveUser(user);
        channelUserRepository.save(channelUser);
        List<User> findUsers = userRepository.findUsersByChannelId(channel.getId());

        //then
        Assertions.assertThat(findUsers.get(0)).isEqualTo(user);
    }

    @Test
    @DisplayName("User 저장 O && channelId 조회 X")
    public void user_saveO_findByChannelIdX() {
        //given
        User user = new User("user", "user");
        Channel channel = new Channel("testChannel");
        ChannelUser channelUser = new ChannelUser();
        channelUser.setChannel(channel);
        channelUser.setUser(user);

        //when
        channelRepository.save(channel);
        userRepository.saveUser(user);
        channelUserRepository.save(channelUser);
        List<User> findUsers = userRepository.findUsersByChannelId("NotExistChannelId");

        //then
        Assertions.assertThat(findUsers.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("User 저장 X && username 조회 X")
    public void user_saveX_findByNameX() {
        //given
        User user = new User("user", "user");

        //when
        List<User> findUsers = userRepository.findUsersByName("user");


        //then
        Assertions.assertThat(findUsers.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("User 저장 X && channelId 조회 X")
    public void user_saveX_findByChannelIdX() {
        //given
        User user = new User("user", "user");
        Channel channel = new Channel("testChannel");
        ChannelUser channelUser = new ChannelUser();
        channelUser.setChannel(channel);

        //when
        userRepository.saveUser(user);
        channelRepository.save(channel);
        channelUserRepository.save(channelUser);
        List<User> findUsers = userRepository.findUsersByChannelId(channel.getId());

        //then
        Assertions.assertThat(findUsers.isEmpty()).isEqualTo(true);
    }
}
