package webrtc.chatservice.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;
import webrtc.chatservice.dto.UserDto.CreateUserRequest;
import webrtc.chatservice.service.channel.ChannelService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class UserServiceImplTest {

    @Autowired
    private UserService userService;
    @Autowired
    private ChannelService channelService;


    @BeforeEach
    public void clearUserCache() {
        userService.redisDataEvict();
    }

    @Test
    @DisplayName("User 저장")
    public void returnUserO() {
        // given
        CreateUserRequest createUserRequest = new CreateUserRequest("user", "user", "email");

        // when
        User saveUser = userService.saveUser(createUserRequest);

        // then
        assertThat(saveUser.getNickname()).isEqualTo("user");
    }

    @Test
    @DisplayName("UserEmail 으로 User 찾기")
    public void findOneUserByUserEmail() {
        //given
        CreateUserRequest createUserRequest = new CreateUserRequest("user", "user", "email");
        User user = userService.saveUser(createUserRequest);

        //when
        User findUser = userService.findOneUserByEmail("email");
        
        //then
        assertThat(user.getId()).isEqualTo(findUser.getId());
    }

    @Test
    @DisplayName("channelId로 Users 찾기")
    public void findUsersByChannelId() {
        //given

        List<String> hashTags = new ArrayList<>();
        hashTags.add("testTag1");
        hashTags.add("testTag2");
        hashTags.add("testTag3");
        CreateUserRequest createUserRequest = new CreateUserRequest("user", "user", "email2");
        User user = userService.saveUser(createUserRequest);

        CreateChannelRequest request = new CreateChannelRequest("testChannel2", hashTags, "chat");
        Channel createChannel = channelService.createChannel(request, "email2");

        //when
        List<User> findUsers = userService.findUsersByChannelId(createChannel.getId());

        //then
        assertThat(findUsers.get(0)).isEqualTo(user);
    }
}
