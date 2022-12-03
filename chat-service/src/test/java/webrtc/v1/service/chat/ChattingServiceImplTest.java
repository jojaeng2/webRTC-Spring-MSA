package webrtc.v1.service.chat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.chat.service.ChatLogService;
import webrtc.v1.chat.service.ChattingServiceImpl;
import webrtc.v1.chat.entity.ChatLog;
import webrtc.v1.user.entity.Users;
import webrtc.v1.enums.ChannelType;
import webrtc.v1.enums.ClientMessageType;
import webrtc.v1.channel.exception.ChannelException.NotExistChannelException;
import webrtc.v1.channel.repository.ChannelCrudRepository;
import webrtc.v1.user.repository.ChannelUserRepository;
import webrtc.v1.user.repository.UsersRepository;
import webrtc.v1.chat.service.factory.ChattingMessageFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static webrtc.v1.enums.ChannelType.*;
import static webrtc.v1.enums.ClientMessageType.*;

@ExtendWith(MockitoExtension.class)
public class ChattingServiceImplTest {

    @InjectMocks
    private ChattingServiceImpl chattingService;

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private ChannelCrudRepository channelCrudRepository;

    @Mock
    private ChannelUserRepository channelUserRepository;


    @Mock
    private ChatLogService chatLogService;

    @Mock
    private ChattingMessageFactory chattingMessageFactory;

    @Mock
    private ChannelTopic channelTopic;


    String nickname1 = "nickname1";
    String nickname2 = "nickname2";
    String password = "password";
    String email1 = "email1";
    String email2 = "email2";
    String channelName1 = "channelName1";
    String chatMessage = "message";
    String topic = "topic";
    String notExistChannelId = "null";
    String tag1 = "tag1";
    String tag2 = "tag2";
    String tag3 = "tag3";
    ChannelType text = TEXT;
    ClientMessageType reenter = REENTER;
    ClientMessageType chat = CHAT;
    Long logIdx = 1L;

    @Test
    void 전송성공() {
        // given
        Channel channel = createChannel(channelName1, text);
        doReturn(Optional.of(channel))
                .when(channelCrudRepository).findById(any(String.class));

        doReturn(createList())
                .when(channelUserRepository).findByChannel(any(Channel.class));
        doReturn(Optional.of(createUsers()))
                .when(usersRepository).findByEmail(any(String.class));

        // when
        chattingService.sendChatMessage(chat, channel.getId(), chatMessage, email1);


        // then

    }
    @Test
    void 전송실패채널없음() {
        // given
        Channel channel = createChannel(channelName1, text);
        doReturn(Optional.empty())
                .when(channelCrudRepository).findById(any(String.class));
        doReturn(Optional.of(createUsers()))
                .when(usersRepository).findByEmail(any(String.class));
        // when

        // then
        Assertions.assertThrows(NotExistChannelException.class,
                () -> chattingService.sendChatMessage(chat, channel.getId(), chatMessage, email1));
    }

    private List<ChatLog> EmptyList() {
        return new ArrayList<>();
    }

    private Channel createChannel(String name, ChannelType type) {
        return Channel.builder()
                .channelName(name)
                .channelType(type)
                .build();
    }

    private List<Users> createList() {
        return new ArrayList<>();
    }

    private Users createUsers() {
        return Users.builder()
                .email(email1)
                .nickname(nickname1)
                .password(password)
                .build();
    }


}
