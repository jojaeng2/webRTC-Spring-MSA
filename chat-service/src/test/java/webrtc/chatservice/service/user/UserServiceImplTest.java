//package webrtc.chatservice.service.user;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.client.ResourceAccessException;
//import webrtc.chatservice.domain.Channel;
//import webrtc.chatservice.domain.ChannelUser;
//import webrtc.chatservice.domain.User;
//import webrtc.chatservice.dto.UserDto.CreateUserRequest;
//import webrtc.chatservice.enums.ChannelType;
//import webrtc.chatservice.repository.channel.ChannelDBRepository;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static webrtc.chatservice.enums.ChannelType.TEXT;
//import static webrtc.chatservice.enums.ChannelType.VOIP;
//
//@SpringBootTest
//public class UserServiceImplTest {
//
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private ChannelDBRepository channelDBRepository;
//
//    String nickname1 = "nickname1";
//    String nickname2 = "nickname2";
//    String password = "password";
//    String email1 = "email1";
//    String email2 = "email2";
//    String channelName1 = "channelName1";
//    String notExistChannelId = "null";
//    String tag1 = "tag1";
//    String tag2 = "tag2";
//    String tag3 = "tag3";
//    ChannelType text = TEXT;
//    ChannelType voip = VOIP;
//
//    @BeforeEach
//    public void clearUserCache() {
//        userService.redisDataEvict();
//    }
//
//    @Test
//    @Transactional
//    public void 유저저장_성공() {
//        // given
//        CreateUserRequest createUserRequest = new CreateUserRequest(nickname1, password, email1);
//
//        // when
//        User saveUser = userService.saveUser(createUserRequest);
//
//        // then
//        assertThat(saveUser.getNickname()).isEqualTo(nickname1);
//    }
//
//    @Test
//    @Transactional
//    public void Email로_유저찾기_성공() {
//        //given
//        CreateUserRequest createUserRequest = new CreateUserRequest(nickname1, password, email1);
//        User user = userService.saveUser(createUserRequest);
//
//        //when
//        User findUser = userService.findOneUserByEmail(email1);
//
//        //then
//        assertThat(user).isEqualTo(findUser);
//    }
//
//    @Test
//    @Transactional
//    public void Email로_유저찾기_실패() {
//        //given
//
//        //when
//
//        //then
//        Assertions.assertThrows(ResourceAccessException.class, () -> {
//            userService.findOneUserByEmail(email1);
//        });
//    }
//
//    @Test
//    @Transactional
//    public void ChannelID로_유저찾기_성공() {
//        //given
//        CreateUserRequest createUserRequest = new CreateUserRequest(nickname1, password, email1);
//        User user = userService.saveUser(createUserRequest);
//
//        Channel channel = new Channel(channelName1, text);
//        channelDBRepository.createChannel(channel, returnHashTags());
//
//        ChannelUser channelUser = new ChannelUser(user, channel);
//
//        //when
//        List<User> findUsers = userService.findUsersByChannelId(channel.getId());
//
//        //then
//        assertThat(findUsers.get(0)).isEqualTo(user);
//    }
//
//    @Test
//    @Transactional
//    public void ChannelID로_유저찾기_실패() {
//        //given
//        CreateUserRequest createUserRequest = new CreateUserRequest(nickname1, password, email1);
//        User user = userService.saveUser(createUserRequest);
//
//        Channel channel = new Channel(channelName1, text);
//        channelDBRepository.createChannel(channel, returnHashTags());
//
//
//        //when
//        List<User> findUsers = userService.findUsersByChannelId(channel.getId());
//
//        //then
//        assertThat(findUsers.isEmpty()).isEqualTo(true);
//    }
//
//    public List<String> returnHashTags() {
//        List<String> hashTags = new ArrayList<>();
//        hashTags.add(tag1);
//        hashTags.add(tag2);
//        hashTags.add(tag3);
//        return hashTags;
//    }
//}
