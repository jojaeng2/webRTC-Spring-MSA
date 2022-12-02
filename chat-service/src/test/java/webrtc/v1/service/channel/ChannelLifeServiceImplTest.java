package webrtc.v1.service.channel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import webrtc.v1.domain.*;
import webrtc.v1.dto.ChannelDto.CreateChannelRequest;
import webrtc.v1.enums.ChannelType;
import webrtc.v1.exception.ChannelException.AlreadyExistChannelException;
import webrtc.v1.exception.ChannelException.NotExistChannelException;
import webrtc.v1.exception.PointException.InsufficientPointException;
import webrtc.v1.exception.UserException.NotExistUserException;
import webrtc.v1.repository.channel.*;
import webrtc.v1.repository.hashtag.ChannelHashTagRepository;
import webrtc.v1.repository.hashtag.HashTagRepository;
import webrtc.v1.repository.users.ChannelUserRepository;
import webrtc.v1.repository.users.UsersRepository;
import webrtc.v1.repository.voice.VoiceRoomRepository;
import webrtc.v1.service.chat.factory.ChattingMessageFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static webrtc.v1.enums.ChannelType.TEXT;

@ExtendWith(MockitoExtension.class)
public class ChannelLifeServiceImplTest {

    @InjectMocks
    private ChannelLifeServiceImpl channelService;


    @Mock
    private ChannelCrudRepository crudRepository;
    @Mock
    private ChannelHashTagRepository channelHashTagRepository;
    @Mock
    private ChannelUserRepository channelUserRepository;
    @Mock
    private ChannelListRepository channelListRepository;
    @Mock
    private VoiceRoomRepository voiceRoomRepository;
    @Spy
    private ChannelRedisRepository channelRedisRepository;

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private HashTagRepository hashTagRepository;
    @Mock
    private ChattingMessageFactory chattingMessageFactory;


    String nickname1 = "nickname1";
    String password = "password";
    String email1 = "email1";
    String email = "email";

    String channelName1 = "channelName1";
    String tag1 = "tag1";
    String tag2 = "tag2";
    String tag3 = "tag3";
    ChannelType text = TEXT;
    Long requestTTL = 10L;

    @Test
    void 채널생성성공_회원존재_태그존재_포인트존재() {
        // given
        doReturn(Optional.of(createUser()))
                .when(usersRepository).findByEmail(any(String.class));
        doReturn(Optional.of(createTag(tag1)))
                .when(hashTagRepository).findByTagName(any(String.class));

        // when
        Channel channel = channelService.createChannel(createChannelRequest(), email);

        //then
        assertThat(channel.getChannelHashTags().size()).isEqualTo(3);
        assertThat(channel.getChannelUsers().size()).isEqualTo(1);
        assertThat(channel.getChatLogs().size()).isEqualTo(1);
    }




    @Test
    void 채널생성성공_회원존재_태그없음_포인트존재() {
        // given
        doReturn(Optional.of(createUser()))
                .when(usersRepository).findByEmail(email1);

        doReturn(Optional.ofNullable(null))
                .when(hashTagRepository).findByTagName(any(String.class));

        // when

        Channel channel = channelService.createChannel(createChannelRequest(), email1);

        // then
        assertThat(channel.getChannelHashTags().size()).isEqualTo(3);
        assertThat(channel.getChannelUsers().size()).isEqualTo(1);
    }



    @Test
    void 채널생성성공_회원통신성공_태그존재_포인트존재() {
        // given


        doReturn(Optional.of(createTag(tag1)))
                .when(hashTagRepository).findByTagName(any(String.class));

        doReturn(Optional.of(createUser()))
                .when(usersRepository).findByEmail(any(String.class));


        // when
        Channel channel = channelService.createChannel(createChannelRequest(), email1);

        // then
        assertThat(channel.getChannelName()).isEqualTo(channelName1);
        assertThat(channel.getChannelHashTags().size()).isEqualTo(3);
    }


    @Test
    void 채널생성실패_채널이름중복() {
        // given
        doReturn(Optional.of(createChannel(channelName1, text)))
                .when(crudRepository).findByChannelName(any(String.class));

        // when

        // then
        assertThrows(AlreadyExistChannelException.class, () -> {
            channelService.createChannel(createChannelRequest(), email1);
        });
    }

    @Test
    void 채널생성실패_회원통신성공_태그존재_포인트부족() {
        // given

        // when

        doReturn(Optional.of(createUserNotPoint()))
                .when(usersRepository).findByEmail(any(String.class));

        // then
        assertThrows(InsufficientPointException.class, () -> channelService.createChannel(createChannelRequest(), email));

    }

    @Test
    void 채널생성실패_회원통신실패() {
        // given

        // when

        // then
        assertThrows(NotExistUserException.class, () -> channelService.createChannel(createChannelRequest(), email));
    }

    @Test
    void 채널삭제성공() {
        // given
        Channel channel = createChannel(channelName1, text);

        doReturn(Optional.of(channel))
                .when(crudRepository).findById(any(String.class));

        // when
        channelService.delete(channel.getId());

        // then

    }

    @Test
    void 채널삭제실패() {
        // given
        Channel channel = createChannel(channelName1, text);

        doReturn(Optional.empty())
                .when(crudRepository).findById(any(String.class));


        // when

        // then
        assertThrows(NotExistChannelException.class, () -> channelService.delete(channel.getId()));
    }

    @Test
    void 수명증가성공() {
        // given
        Channel channel = createChannel(channelName1, text);

        doReturn(Optional.of(channel))
                .when(crudRepository).findById(any(String.class));
        doReturn(Optional.of(createUser()))
                .when(usersRepository).findByEmail(any(String.class));
        // when

        // then
        channelService.extensionChannelTTL(channel.getId(), email, requestTTL);
    }

    @Test
    void 수명증가실패채널없음() {
        // given
        Channel channel = createChannel(channelName1, text);

        doReturn(Optional.empty())
                .when(crudRepository).findById(any(String.class));

        // when

        // then
        assertThrows(NotExistChannelException.class, () -> channelService.extensionChannelTTL(channel.getId(), email, requestTTL));
    }

    @Test
    void 수명증가실패포인트부족() {
        // given
        Channel channel = createChannel(channelName1, text);

        doReturn(Optional.of(channel))
                .when(crudRepository).findById(any(String.class));
        doReturn(Optional.of(createUserNotPoint()))
                .when(usersRepository).findByEmail(any(String.class));
        // when

        // then
        assertThrows(InsufficientPointException.class, () -> channelService.extensionChannelTTL(channel.getId(), email, requestTTL));
    }

    @Test
    void 수명증가실패회원없음() {
        // given
        Channel channel = createChannel(channelName1, text);

        doReturn(Optional.of(channel))
                .when(crudRepository).findById(any(String.class));

        // when

        // then
        assertThrows(NotExistUserException.class, () -> channelService.extensionChannelTTL(channel.getId(), email, requestTTL));
    }

    private CreateChannelRequest createChannelRequest() {
        List<String> hashTags = new ArrayList<>();
        hashTags.add(tag1);
        hashTags.add(tag2);
        hashTags.add(tag3);

        return new CreateChannelRequest(channelName1, hashTags, text);
    }

    private Users createUser() {
        Users user = Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();
        Point point = Point.builder()
                .message("회원 가입")
                .amount(1000000)
                .build();
        user.addPoint(point);
        return user;
    }

    private Users createUserNotPoint() {
        return Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();
    }

    private HashTag createTag(String name) {
        return HashTag.builder()
                .tagName(name).build();
    }

    private Channel createChannel(String name, ChannelType type) {
        return Channel.builder()
                .channelName(name)
                .channelType(type)
                .build();
    }
}
