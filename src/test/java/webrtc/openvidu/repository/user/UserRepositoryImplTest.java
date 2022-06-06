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
        User user = new User("user", "user", "email");

        // when
        userRepository.saveUser(user);

        // then

    }

    @Test
    @DisplayName("User 저장 O && userEmail로 조회 성공")
    public void user_saveO_findByUserEmailSuccess() {
        //given
        User user = new User("user", "user", "email");

        //when
        userRepository.saveUser(user);
        List<User> findUsers = userRepository.findUsersByEmail(user.getEmail());

        //then
        Assertions.assertThat(findUsers.get(0)).isEqualTo(user);
    }

    @Test
    @DisplayName("User 저장 O && userEmail로 조회 실패")
    public void user_saveO_findByUserEmailFail() {
        //given
        User user = new User("user", "user", "email");

        //when
        userRepository.saveUser(user);
        List<User> findUsers = userRepository.findUsersByEmail("email2");


        //then
        Assertions.assertThat(findUsers.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("User 저장 O && channelId 조회 성공")
    public void user_saveO_findByChannelIdSuccess() {
        //given
        User user = new User("user", "user", "email");
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
    @DisplayName("User 저장 O && channelId 조회 실패")
    public void user_saveO_findByChannelIdFail() {
        //given
        User user = new User("user", "user", "email");
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
    @DisplayName("User 저장 X && userEmail 조회 실패")
    public void user_saveX_findByUserEmailFail() {
        //given
        User user = new User("user", "user", "email");

        //when
        List<User> findUsers = userRepository.findUsersByEmail("user");


        //then
        Assertions.assertThat(findUsers.isEmpty()).isEqualTo(true);
    }

    @Test
    @DisplayName("User 저장 X && channelId 조회 실패")
    public void user_saveX_findByChannelIdFail() {
        //given
        User user = new User("user", "user", "email");
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
