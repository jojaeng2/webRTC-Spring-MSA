package webrtc.v1.chat.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static webrtc.v1.chat.enums.ClientMessageType.CHAT;
import static webrtc.v1.chat.enums.ClientMessageType.REENTER;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.exception.ChannelException.NotExistChannelException;
import webrtc.v1.channel.repository.ChannelCrudRepository;
import webrtc.v1.channel.repository.ChannelUserRepository;
import webrtc.v1.chat.dto.ChatDto.SendChatDto;
import webrtc.v1.chat.factory.ChattingMessageFactory;
import webrtc.v1.staticgenarator.ChannelGenerator;
import webrtc.v1.staticgenarator.UserGenerator;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.repository.UsersRepository;

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


  String chatMessage = "message";

  @Test
  void 전송성공TypeCHAT() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();
    Users user = UserGenerator.createUsers();
    doReturn(Optional.of(channel))
        .when(channelCrudRepository).findById(any(String.class));

    doReturn(createList())
        .when(channelUserRepository).findByChannel(any(Channel.class));
    doReturn(Optional.of(user))
        .when(usersRepository).findById(any(String.class));

    // when
    chattingService.send(createSendChatDtoTypeCHAT(channel, user));

    // then

  }

  @Test
  void 전송성공TypeREENTER() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();
    Users user = UserGenerator.createUsers();
    doReturn(Optional.of(channel))
        .when(channelCrudRepository).findById(any(String.class));

    doReturn(createList())
        .when(channelUserRepository).findByChannel(any(Channel.class));
    doReturn(Optional.of(user))
        .when(usersRepository).findById(any(String.class));

    // when
    chattingService.send(createSendChatDtoTypeREENTER(channel, user));

    // then

  }

  @Test
  void 전송실패채널없음() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();
    Users user = UserGenerator.createUsers();
    doReturn(Optional.empty())
        .when(channelCrudRepository).findById(any(String.class));
    doReturn(Optional.of(user))
        .when(usersRepository).findById(any(String.class));
    // when

    // then
    Assertions.assertThrows(NotExistChannelException.class,
        () -> chattingService.send(createSendChatDtoTypeCHAT(channel, user)));
  }

  @Test
  void closeChannel성공() {
    // given
    Channel channel = ChannelGenerator.createTextChannel();
    // when
    chattingService.closeChannel(channel);

    // then

  }

  private List<Users> createList() {
    return new ArrayList<>();
  }

  private SendChatDto createSendChatDtoTypeCHAT(Channel channel, Users user) {
    return new SendChatDto(CHAT, channel.getId(), chatMessage, user.getId());
  }

  private SendChatDto createSendChatDtoTypeREENTER(Channel channel, Users user) {
    return new SendChatDto(REENTER, channel.getId(), chatMessage, user.getId());
  }


}
