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
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.ChannelException;
import webrtc.chatservice.exception.ChannelUserException;
import webrtc.chatservice.exception.UserException;
import webrtc.chatservice.repository.channel.*;
import webrtc.chatservice.repository.chat.ChatLogRepository;
import webrtc.chatservice.repository.hashtag.HashTagRepository;
import webrtc.chatservice.repository.users.UsersRepository;
import webrtc.chatservice.service.rabbit.RabbitPublish;

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
    private ChannelHashTagRepository channelHashTagRepository;
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
    String nickname2 = "nickname2";
    String password = "password";
    String email1 = "email1";
    String email2 = "email2";
    String channelName1 = "channelName1";
    String channelName2 = "channelName2";
    String tag1 = "tag1";
    String tag2 = "tag2";
    String tag3 = "tag3";
    ChannelType text = TEXT;
    ChannelType voip = VOIP;


//    @Test
//    @Transactional
//    public void 채널입장성공_유저이미존재_처음입장() {
//        // given
//        Channel channel = new Channel(channelName1, text);
//
//        doReturn(new Users(nickname1, password, email1))
//                .when(usersRepository).findUserByEmail(email1);
//
//        doThrow(new NotExistChannelException())
//                .when(channelListRepository).findChannelsByChannelIdAndUserId(any(String.class), any(String.class));
//
//        // when
//
//        // then
//        Channel enterChannel = channelService.enterChannel(channel.getId(), email1);
//        assertThat(enterChannel.getCurrentParticipants()).isEqualTo(1);
//    }

//    @Test
//    @Transactional
//    public void 채널입장실패_유저이미존재_이전에입장() {
//        // given
//        Channel channel = new Channel(channelName1, text);
//
//        doReturn(new Users(nickname1, password, email1))
//                .when(usersRepository).findUserByEmail(email1);
//
//        doReturn(new ArrayList<>())
//                .when(channelListRepository).findChannelsByChannelIdAndUserId(any(String.class), any(String.class));
//
//        // when
//
//        // then
//        assertThat(channel.getCurrentParticipants()).isEqualTo(0);
//        assertThrows(AlreadyExistUserInChannelException.class, ()->{
//            channelService.enterChannel(channel.getId(), email1);
//        });
//    }

//    @Test
//    @Transactional
//    public void 채널입장실패_유저이미존재_인원제한() {
//        // given
//        Channel channel = new Channel(channelName1, text);
//        doReturn(new Users(nickname1, password, email1))
//                .when(usersRepository).findUserByEmail(any(String.class));
//
//        doReturn(111L)
//                .when(channelRedisRepository).findChannelTTL(channel.getId());
//
//        doThrow(new NotExistChannelException())
//                .when(channelListRepository).findChannelsByChannelIdAndUserId(any(String.class), any(String.class));
//
//        for(int i=0; i<14; i++) {
//            channelService.enterChannel(channel.getId(), email1+ i);
//
//        }
//        // when
//
//        // then
//        assertThrows(ChannelParticipantsFullException.class, ()->{
//            channelService.enterChannel(channel.getId(), email1);
//        });
//    }

//    @Test
//    @Transactional
//    public void 채널입장성공_유저통신성공() {
//        // given
//        Channel channel = new Channel(channelName1, text);
//
//        doThrow(new NotExistUserException())
//                .when(usersRepository).findUserByEmail(any(String.class));
//
//        doReturn(new Users(nickname1, password, email1))
//                .when(httpApiController).postFindUserByEmail(email1);
//        doNothing()
//                .when(usersRepository).saveUser(any(Users.class));
//
//        doThrow(new NotExistChannelException())
//                .when(channelListRepository).findChannelsByChannelIdAndUserId(any(String.class), any(String.class));
//
//        // when
//
//        // then
//        Channel enterChannel = channelService.enterChannel(channel.getId(), email1);
//
//        assertThat(enterChannel.getCurrentParticipants()).isEqualTo(1);
//    }

    @Test
    @Transactional
    public void 채널입장실패_유저통신실패() {
        // given
        Channel channel = new Channel(channelName1, text);

        doReturn(Optional.empty())
                .when(usersRepository).findUserByEmail(any(String.class));

        doThrow(new UserException.NotExistUserException())
                .when(httpApiController).postFindUserByEmail(email1);

        // when

        // then
        assertThrows(UserException.NotExistUserException.class, ()-> {
            channelIOService.enterChannel(channel.getId(), email1);
        });
    }



    @Test
    @Transactional
    public void 채널퇴장성공() {
        // given
        Users users = new Users(nickname1, password, email1);
        Channel channel = new Channel(channelName1, text);

        doReturn(Optional.of(channel))
                .when(channelCrudRepository).findById(channel.getId());
        doReturn(Optional.of(new ChannelUser(users, channel)))
                .when(channelUserRepository).findOneChannelUser(any(String.class), any(String.class));

        // when
        channelIOService.exitChannel(channel.getId(), users.getId());

        // then
    }


    @Test
    @Transactional
    public void 채널퇴장실패_채널없음() {
        // given
        Channel channel = new Channel(channelName1, text);
        Users users = new Users(nickname1, password, email1);

        doThrow(new ChannelException.NotExistChannelException())
                .when(channelCrudRepository).findById(channel.getId());

        // when

        // then
        assertThrows(ChannelException.NotExistChannelException.class, ()->{
            channelIOService.exitChannel(channel.getId(), users.getId());
        });

    }

    @Test
    @Transactional
    public void 채널퇴장실패_채널유저없음() {
        // given
        Channel channel = new Channel(channelName1, text);
        Users users = new Users(nickname1, password, email1);

        doReturn(Optional.of(channel))
                .when(channelCrudRepository).findById(channel.getId());

        doThrow(new ChannelUserException.NotExistChannelUserException())
                .when(channelUserRepository).findOneChannelUser(any(String.class), any(String.class));

        // when

        // then
        assertThrows(ChannelUserException.NotExistChannelUserException.class, ()->{
            channelIOService.exitChannel(channel.getId(), users.getId());
        });

    }

}
