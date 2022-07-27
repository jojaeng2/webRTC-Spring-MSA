package webrtc.chatservice.service.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.UserDto.DecreasePointRequest;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.ChannelException;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.exception.PointException;
import webrtc.chatservice.exception.PointException.InsufficientPointException;
import webrtc.chatservice.exception.UserException;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.channel.ChannelHashTagRepository;
import webrtc.chatservice.repository.channel.ChannelDBRepository;
import webrtc.chatservice.repository.channel.ChannelRedisRepository;
import webrtc.chatservice.repository.channel.ChannelUserRepository;
import webrtc.chatservice.repository.chat.ChatLogRepository;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.utils.CustomJsonMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static webrtc.chatservice.enums.ChannelType.TEXT;

@ExtendWith(MockitoExtension.class)
public class PointDecreaseMockTest {

    String nickname1 = "nickname1";
    String password = "password";
    String email1 = "email1";
    String channelName1 = "channelName1";
    ChannelType text = TEXT;


    @InjectMocks
    private ChannelServiceImpl channelService;
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
        User user = new User(nickname1, password, email1);
        Long requestTTL = 100L;

        doReturn(channel)
                .when(channelDBRepository).findChannelById(channel.getId());
        doNothing()
                .when(httpApiController).postDecreaseUserPoint(any(String.class), any(Long.class));
        doNothing()
                .when(channelRedisRepository).extensionChannelTTL(any(Channel.class), any(Long.class));

        // when
        channelService.extensionChannelTTL(channel.getId(), user.getEmail(), requestTTL);

        // then

    }

    @Test
    @Transactional
    public void 채널수명연장실패_채널없음() {
        // given
        Channel channel = new Channel(channelName1, text);
        User user = new User(nickname1, password, email1);
        Long requestTTL = 100L;

        doThrow(new NotExistChannelException())
                .when(channelDBRepository).findChannelById(channel.getId());

        // when

        // then
        assertThrows(NotExistChannelException.class, ()-> {
            channelService.extensionChannelTTL(channel.getId(), user.getEmail(), requestTTL);
        });
    }

    @Test
    @Transactional
    public void 채널수명연장실패_유저없음() {
        // given
        Channel channel = new Channel(channelName1, text);
        User user = new User(nickname1, password, email1);
        Long requestTTL = 100L;

        doReturn(channel)
                .when(channelDBRepository).findChannelById(any(String.class));
        doThrow(new NotExistUserException())
                .when(httpApiController).postDecreaseUserPoint(any(String.class), any(Long.class));

        // when

        // then
        assertThrows(NotExistUserException.class, ()-> {
            channelService.extensionChannelTTL(channel.getId(), user.getEmail(), requestTTL);
        });
    }

    @Test
    @Transactional
    public void 채널수명연장실패_포인트부족() {
        // given
        Channel channel = new Channel(channelName1, text);
        User user = new User(nickname1, password, email1);
        Long requestTTL = 100L;

        doReturn(channel)
                .when(channelDBRepository).findChannelById(any(String.class));
        doThrow(new InsufficientPointException())
                .when(httpApiController).postDecreaseUserPoint(any(String.class), any(Long.class));

        // when

        // then
        assertThrows(InsufficientPointException.class, ()-> {
            channelService.extensionChannelTTL(channel.getId(), user.getEmail(), requestTTL);
        });
    }


}