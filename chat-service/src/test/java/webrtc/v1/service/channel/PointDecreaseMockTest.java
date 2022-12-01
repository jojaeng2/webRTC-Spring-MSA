package webrtc.v1.service.channel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.domain.Channel;
import webrtc.v1.domain.Point;
import webrtc.v1.domain.Users;
import webrtc.v1.enums.ChannelType;
import webrtc.v1.exception.ChannelException.NotExistChannelException;
import webrtc.v1.exception.PointException.InsufficientPointException;
import webrtc.v1.exception.UserException.NotExistUserException;
import webrtc.v1.repository.channel.ChannelCrudRepository;
import webrtc.v1.repository.channel.ChannelListRepository;
import webrtc.v1.repository.channel.ChannelRedisRepository;
import webrtc.v1.repository.users.UsersRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static webrtc.v1.enums.ChannelType.TEXT;

@ExtendWith(MockitoExtension.class)
public class PointDecreaseMockTest {

    String nickname1 = "nickname1";
    String password = "password";
    String email1 = "email1";
    String channelName1 = "channelName1";
    ChannelType text = TEXT;


    @InjectMocks
    private ChannelLifeServiceImpl channelService;
    @Mock
    private ChannelListRepository channelListRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private ChannelRedisRepository channelRedisRepository;
    @Mock
    private ChannelCrudRepository channelCrudRepository;

    @Test
    @Transactional
    public void 채널수명연장성공() {
        // given
        Channel channel = Channel.builder()
                .channelName(channelName1)
                .channelType(text)
                .build();
        Users users2 = Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();
        Long requestTTL = 100L;

        Point point = Point.builder()
                .message("회원 가입")
                .amount(1000000)
                .build();
        users2.addPoint(point);

        doReturn(Optional.of(channel))
                .when(channelCrudRepository).findById(channel.getId());
        doReturn(Optional.of(users2))
                .when(usersRepository).findByEmail(any(String.class));
        doNothing()
                .when(channelRedisRepository).extensionChannelTTL(any(Channel.class), any(Long.class));

        // when
        channelService.extensionChannelTTL(channel.getId(), users2.getEmail(), requestTTL);

        // then

    }

    @Test
    @Transactional
    public void 채널수명연장실패_채널없음() {
        // given
        Channel channel = Channel.builder()
                .channelName(channelName1)
                .channelType(text)
                .build();
        Users users2 = Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();
        Long requestTTL = 100L;

        doReturn(Optional.empty())
                .when(channelCrudRepository).findById(channel.getId());

        // when

        // then
        assertThrows(NotExistChannelException.class, ()-> {
            channelService.extensionChannelTTL(channel.getId(), users2.getEmail(), requestTTL);
        });
    }

    @Test
    @Transactional
    public void 채널수명연장실패_유저없음() {
        // given
        Channel channel = Channel.builder()
                .channelName(channelName1)
                .channelType(text)
                .build();
        Users users2 = Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();
        Long requestTTL = 100L;

        doReturn(Optional.of(channel))
                .when(channelCrudRepository).findById(any(String.class));

        // when

        // then
        assertThrows(NotExistUserException.class, ()-> {
            channelService.extensionChannelTTL(channel.getId(), users2.getEmail(), requestTTL);
        });
    }

    @Test
    @Transactional
    public void 채널수명연장실패_포인트부족() {
        // given
        Channel channel = Channel.builder()
                .channelName(channelName1)
                .channelType(text)
                .build();
        Users users2 = Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();
        Long requestTTL = 100L;

        doReturn(Optional.of(channel))
                .when(channelCrudRepository).findById(any(String.class));
        doReturn(Optional.of(users2))
                .when(usersRepository).findByEmail(any(String.class));

        // when

        // then
        assertThrows(InsufficientPointException.class, ()-> {
            channelService.extensionChannelTTL(channel.getId(), users2.getEmail(), requestTTL);
        });
    }
}