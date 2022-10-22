package webrtc.chatservice.service.channel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.exception.PointException.InsufficientPointException;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.channel.ChannelCrudRepository;
import webrtc.chatservice.repository.channel.ChannelListRepository;
import webrtc.chatservice.repository.channel.ChannelRedisRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static webrtc.chatservice.enums.ChannelType.TEXT;

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
    private ChannelRedisRepository channelRedisRepository;
    @Mock
    private ChannelCrudRepository channelCrudRepository;
    @Mock
    private HttpApiController httpApiController;

    @Test
    @Transactional
    public void 채널수명연장성공() {
        // given
        Channel channel = Channel.builder()
                .channelName(channelName1)
                .channelType(text)
                .build();
        Users users = Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();
        Long requestTTL = 100L;

        doReturn(Optional.of(channel))
                .when(channelCrudRepository).findById(channel.getId());
        doNothing()
                .when(httpApiController).postDecreaseUserPoint(any(String.class), any(Long.class), any(String.class));
        doNothing()
                .when(channelRedisRepository).extensionChannelTTL(any(Channel.class), any(Long.class));

        // when
        channelService.extensionChannelTTL(channel.getId(), users.getEmail(), requestTTL);

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
        Users users = Users.builder()
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
            channelService.extensionChannelTTL(channel.getId(), users.getEmail(), requestTTL);
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
        Users users = Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();
        Long requestTTL = 100L;

        doReturn(Optional.of(channel))
                .when(channelCrudRepository).findById(any(String.class));
        doThrow(new NotExistUserException())
                .when(httpApiController).postDecreaseUserPoint(any(String.class), any(long.class), any(String.class));

        // when

        // then
        assertThrows(NotExistUserException.class, ()-> {
            channelService.extensionChannelTTL(channel.getId(), users.getEmail(), requestTTL);
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
        Users users = Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();
        Long requestTTL = 100L;

        doReturn(Optional.of(channel))
                .when(channelCrudRepository).findById(any(String.class));
        doThrow(new InsufficientPointException())
                .when(httpApiController).postDecreaseUserPoint(any(String.class), any(Long.class), any(String.class));

        // when

        // then
        assertThrows(InsufficientPointException.class, ()-> {
            channelService.extensionChannelTTL(channel.getId(), users.getEmail(), requestTTL);
        });
    }


}