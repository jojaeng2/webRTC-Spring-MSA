package webrtc.v1.service.channel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.repository.ChannelCrudRepository;
import webrtc.v1.channel.repository.ChannelListRepository;
import webrtc.v1.channel.repository.ChannelRedisRepository;
import webrtc.v1.channel.service.ChannelIOServiceImpl;
import webrtc.v1.channel.entity.ChannelUser;
import webrtc.v1.user.entity.Users;
import webrtc.v1.channel.enums.ChannelType;
import webrtc.v1.channel.exception.ChannelException.AlreadyExistUserInChannelException;
import webrtc.v1.channel.exception.ChannelException.ChannelParticipantsFullException;
import webrtc.v1.channel.exception.ChannelException.NotExistChannelException;
import webrtc.v1.channel.exception.ChannelUserException.NotExistChannelUserException;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.chat.repository.ChatLogRepository;
import webrtc.v1.hashtag.repository.HashTagRepository;
import webrtc.v1.user.repository.ChannelUserRepository;
import webrtc.v1.user.repository.UsersRepository;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static webrtc.v1.channel.enums.ChannelType.TEXT;

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
    private UsersRepository usersRepository;
    @Mock
    private ChannelUserRepository channelUserRepository;
    @Mock
    private HashTagRepository hashTagRepository;


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
        channelIOService.enterChannel(channel.getId(), user.getEmail());

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

        // when

        // then
        assertThrows(NotExistUserException.class, () -> channelIOService.enterChannel(channel.getId(), user.getEmail()));
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
        assertThrows(NotExistChannelException.class, () -> channelIOService.enterChannel(channel.getId(), user.getEmail()));
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
        assertThrows(AlreadyExistUserInChannelException.class, () -> channelIOService.enterChannel(channel.getId(), user.getEmail()));
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
        assertThrows(ChannelParticipantsFullException.class, () -> channelIOService.enterChannel(channel.getId(), user.getEmail()));
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
                .when(usersRepository).findById(any(UUID.class));
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
        doReturn(Optional.of(user))
                .when(usersRepository).findById(any(UUID.class));

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
                .when(usersRepository).findById(any(UUID.class));
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
        return Channel.builder()
                .channelName(name)
                .channelType(type)
                .build();
    }

    private ChannelUser createChannelUser(Channel channel, Users user) {
        ChannelUser channelUser = ChannelUser.builder()
                .user(user)
                .channel(channel)
                .build();
        channel.enterChannelUser(channelUser);
        return channelUser;
    }
}
