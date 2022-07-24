package webrtc.chatservice.repository.channel;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.ChannelUserException;
import webrtc.chatservice.exception.ChannelUserException.NotExistChannelUserException;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.service.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ChannelType.VOIP;


@SpringBootTest
public class ChannelUserRepositoryImplTest {

    @Autowired
    private ChannelUserRepository channelUserRepository;
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
    String notExistChannelId = "null";
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


    @Test
    @Transactional
    public void 채널유저_저장후_채널ID_AND_유저ID로_조회_성공() {
        //given
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, returnHashTags());
        User user = userRepository.findUserByEmail(email1);
        ChannelUser channelUser = new ChannelUser(user, channel);
        channelRepository.enterChannelUserInChannel(channel, channelUser);

        //when

        ChannelUser findChannelUser = channelUserRepository.findOneChannelUser(channel.getId(), user.getId());

        //then
        assertThat(findChannelUser).isEqualTo(channelUser);
        assertThat(findChannelUser.getChannel().getId()).isEqualTo(channel.getId());
        assertThat(findChannelUser.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @Transactional
    public void 채널유저_저장후_채널ID_AND_유저ID로_조회_실패() {
        //given
        Channel channel = new Channel(channelName1, text);
        channelRepository.createChannel(channel, returnHashTags());
        User user = userRepository.findUserByEmail(email1);
        ChannelUser channelUser = new ChannelUser(user, channel);
        channelRepository.enterChannelUserInChannel(channel, channelUser);

        //when

        //then
        assertThrows(NotExistChannelUserException.class, () -> {
            channelUserRepository.findOneChannelUser(channel.getId(), "notExist");
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
