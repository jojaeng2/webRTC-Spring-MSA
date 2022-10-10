package webrtc.chatservice.service.channel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.HashTag;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.ChannelDto;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.ChannelException;
import webrtc.chatservice.exception.ChannelException.AlreadyExistChannelException;
import webrtc.chatservice.exception.ChannelException.AlreadyExistUserInChannelException;
import webrtc.chatservice.exception.ChannelException.ChannelParticipantsFullException;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.exception.ChannelUserException;
import webrtc.chatservice.exception.ChannelUserException.NotExistChannelUserException;
import webrtc.chatservice.exception.UserException;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.channel.*;
import webrtc.chatservice.repository.chat.ChatLogRepository;
import webrtc.chatservice.repository.hashtag.HashTagRepository;
import webrtc.chatservice.repository.users.ChannelUserRepository;
import webrtc.chatservice.repository.users.UsersRepository;
import webrtc.chatservice.service.rabbit.RabbitPublish;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ChannelType.VOIP;

@ExtendWith(MockitoExtension.class)
public class ChannelIOServiceImplTest {

    @InjectMocks
    private ChannelIOServiceImpl channelIOService;
    @Mock
    private ChannelListRepository channelListRepository;
    @Mock
    private ChannelRedisRepository channelRedisRepository;
    @Mock
    private ChatLogRepository chatLogRepository;
    @Mock
    private ChannelCrudRepository channelCrudRepository;
    @Mock
    private RabbitPublish rabbitPublish;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private ChannelUserRepository channelUserRepository;
    @Mock
    private HashTagRepository hashTagRepository;
    @Mock
    private HttpApiController httpApiController;


    String nickname1 = "nickname1";
    String password = "password";
    String email1 = "email1";
    String channelName1 = "channelName1";
    ChannelType text = TEXT;


    @Test
    void 채널입장성공() {
        // given
        Channel channel = createChannel(channelName1, text);
        Users user = createUser();

        doReturn(Optional.of(user))
                .when(usersRepository).findByEmail(any(String.class));
        doReturn(Optional.of(channel))
                .when(channelCrudRepository).findById(any(String.class));

        // when
        channelIOService.enterChannel(channel.getId(), user.getId());

        // then
        assertThat(channel.getChannelUsers().size()).isEqualTo(1);
    }

    @Test
    void 채널입장실패유저없음() {
        // given
        Channel channel = createChannel(channelName1, text);
        Users user = createUser();

        doReturn(Optional.empty())
                .when(usersRepository).findByEmail(any(String.class));
        doThrow(new NotExistUserException())
                .when(httpApiController).postFindUserByEmail(any(String.class));

        // when

        // then
        assertThrows(NotExistUserException.class, () -> channelIOService.enterChannel(channel.getId(), user.getId()));
    }

    @Test
    void 채널입장실패채널없음() {
        // given
        Channel channel = createChannel(channelName1, text);
        Users user = createUser();

        doReturn(Optional.of(user))
                .when(usersRepository).findByEmail(any(String.class));

        doThrow(new NotExistChannelException())
                .when(channelCrudRepository).findById(any(String.class));
        // when

        // then
        assertThrows(NotExistChannelException.class, () -> channelIOService.enterChannel(channel.getId(), user.getId()));
    }

    @Test
    void 채널입장실패이미입장() {
        // given
        Channel channel = createChannel(channelName1, text);
        Users user = createUser();
        ChannelUser channelUser = createChannelUser(channel, user);

        doReturn(Optional.of(user))
                .when(usersRepository).findByEmail(any(String.class));
        doReturn(Optional.of(channel))
                .when(channelCrudRepository).findById(any(String.class));
        doReturn(Optional.of(channelUser))
                .when(channelUserRepository).findByChannelAndUser(any(Channel.class), any(Users.class));

        // when

        // then
        assertThrows(AlreadyExistUserInChannelException.class, () -> channelIOService.enterChannel(channel.getId(), user.getId()));
    }

    @Test
    void 채널입장실패인원가득() {
        // given
        Channel channel = createChannel(channelName1, text);
        Users user = createUser();
        channel.setCurrentParticipants(15L);
        doReturn(Optional.of(user))
                .when(usersRepository).findByEmail(any(String.class));
        doReturn(Optional.of(channel))
                .when(channelCrudRepository).findById(any(String.class));


        // when

        // then
        assertThrows(ChannelParticipantsFullException.class, () -> channelIOService.enterChannel(channel.getId(), user.getId()));
    }

    @Test
    void 채널퇴장성공() {
        // given
        Channel channel = createChannel(channelName1, text);
        Users user = createUser();
        ChannelUser channelUser = createChannelUser(channel, user);

        // when
        doReturn(Optional.of(channel))
                .when(channelCrudRepository).findById(any(String.class));
        doReturn(Optional.of(user))
                .when(usersRepository).findById(any(String.class));
        doReturn(Optional.of(channelUser))
                .when(channelUserRepository).findByChannelAndUser(any(Channel.class), any(Users.class));

        channelIOService.exitChannel(channel.getId(), user.getId());

        // then
        assertThat(channel.getChannelUsers()).isEmpty();
    }

    @Test
    void 채널퇴장실패채널없음() {
        // given
        Channel channel = createChannel(channelName1, text);
        Users user = createUser();
        ChannelUser channelUser = createChannelUser(channel, user);

        // when
        doReturn(Optional.ofNullable(null))
                .when(channelCrudRepository).findById(any(String.class));

        // then
        assertThrows(NotExistChannelException.class, () -> channelIOService.exitChannel(channel.getId(), user.getId()));
    }

    @Test
    void 채널퇴장실패채널유저없음() {
        // given
        Channel channel = createChannel(channelName1, text);
        Users user = createUser();
        ChannelUser channelUser = createChannelUser(channel, user);

        // when
        doReturn(Optional.of(channel))
                .when(channelCrudRepository).findById(any(String.class));
        doReturn(Optional.of(user))
                .when(usersRepository).findById(any(String.class));
        doReturn(Optional.empty())
                .when(channelUserRepository).findByChannelAndUser(any(Channel.class), any(Users.class));

        // then
        assertThrows(NotExistChannelUserException.class, () -> channelIOService.exitChannel(channel.getId(), user.getId()));
    }



    private Users createUser() {
        return Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();
    }

    private Channel createChannel(String name, ChannelType type) {
        return new Channel(name, type);
    }

    private ChannelUser createChannelUser(Channel channel, Users user) {
        return new ChannelUser(user, channel);
    }
}
