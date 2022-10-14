package webrtc.chatservice.service.chat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChatLog;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.enums.ClientMessageType;
import webrtc.chatservice.exception.ChannelException;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.repository.channel.ChannelCrudRepository;
import webrtc.chatservice.repository.users.ChannelUserRepository;
import webrtc.chatservice.repository.users.UsersRepository;
import webrtc.chatservice.service.chat.factory.ChattingMessageFactory;
import webrtc.chatservice.service.rabbit.RabbitPublish;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static webrtc.chatservice.enums.ChannelType.*;
import static webrtc.chatservice.enums.ClientMessageType.*;

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
    private RabbitPublish rabbitPublish;

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


        // when
        chattingService.sendChatMessage(chat, channel.getId(), nickname1, chatMessage, email1);


        // then

    }
    @Test
    void 전송실패채널없음() {
        // given
        Channel channel = createChannel(channelName1, text);
        doReturn(Optional.empty())
                .when(channelCrudRepository).findById(any(String.class));

        // when

        // then
        Assertions.assertThrows(NotExistChannelException.class,
                () -> chattingService.sendChatMessage(chat, channel.getId(), nickname1, chatMessage, email1));
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


}
