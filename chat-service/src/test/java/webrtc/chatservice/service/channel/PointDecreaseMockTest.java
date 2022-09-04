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
import webrtc.chatservice.repository.channel.ChannelDBRepository;
import webrtc.chatservice.repository.channel.ChannelRedisRepository;

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
    private ChannelDBRepository channelDBRepository;
    @Mock
    private ChannelRedisRepository channelRedisRepository;
    @Mock
    private HttpApiController httpApiController;

    @Test
    @Transactional
    public void 채널수명연장성공() {
        // given
        Channel channel = new Channel(channelName1, text);
        Users users = new Users(nickname1, password, email1);
        Long requestTTL = 100L;

        doReturn(channel)
                .when(channelDBRepository).findChannelById(channel.getId());
        doNothing()
                .when(httpApiController).postDecreaseUserPoint(any(String.class), any(Long.class));
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
        Channel channel = new Channel(channelName1, text);
        Users users = new Users(nickname1, password, email1);
        Long requestTTL = 100L;

        doThrow(new NotExistChannelException())
                .when(channelDBRepository).findChannelById(channel.getId());

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
        Channel channel = new Channel(channelName1, text);
        Users users = new Users(nickname1, password, email1);
        Long requestTTL = 100L;

        doReturn(channel)
                .when(channelDBRepository).findChannelById(any(String.class));
        doThrow(new NotExistUserException())
                .when(httpApiController).postDecreaseUserPoint(any(String.class), any(Long.class));

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
        Channel channel = new Channel(channelName1, text);
        Users users = new Users(nickname1, password, email1);
        Long requestTTL = 100L;

        doReturn(channel)
                .when(channelDBRepository).findChannelById(any(String.class));
        doThrow(new InsufficientPointException())
                .when(httpApiController).postDecreaseUserPoint(any(String.class), any(Long.class));

        // when

        // then
        assertThrows(InsufficientPointException.class, ()-> {
            channelService.extensionChannelTTL(channel.getId(), users.getEmail(), requestTTL);
        });
    }


}