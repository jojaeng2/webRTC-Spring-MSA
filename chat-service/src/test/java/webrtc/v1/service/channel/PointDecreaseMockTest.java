package webrtc.v1.service.channel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.service.ChannelLifeServiceImpl;
import webrtc.v1.point.entity.Point;
import webrtc.v1.point.repository.PointRepository;
import webrtc.v1.user.entity.Users;
import webrtc.v1.channel.enums.ChannelType;
import webrtc.v1.channel.exception.ChannelException.NotExistChannelException;
import webrtc.v1.point.exception.PointException.InsufficientPointException;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.channel.repository.ChannelCrudRepository;
import webrtc.v1.channel.repository.ChannelListRepository;
import webrtc.v1.channel.repository.ChannelRedisRepository;
import webrtc.v1.user.repository.UsersRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static webrtc.v1.channel.enums.ChannelType.TEXT;

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
    @Mock
    private PointRepository pointRepository;

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
                .when(usersRepository).findById(any(String.class));
        doReturn(List.of(point))
                .when(pointRepository).findByUser(any(Users.class));
        doNothing()
                .when(channelRedisRepository).extensionTtl(any(Channel.class), any(Long.class));

        // when
        channelService.extension(channel.getId(), users2.getId(), requestTTL);

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
            channelService.extension(channel.getId(), users2.getId(), requestTTL);
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
            channelService.extension(channel.getId(), users2.getId(), requestTTL);
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
                .when(usersRepository).findById(any(String.class));

        // when

        // then
        assertThrows(InsufficientPointException.class, ()-> {
            channelService.extension(channel.getId(), users2.getId(), requestTTL);
        });
    }
}