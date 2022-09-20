package webrtc.chatservice.service.channel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.HashTag;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.ChannelDto.ChannelResponse;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.exception.HashTagException;
import webrtc.chatservice.exception.HashTagException.NotExistHashTagException;
import webrtc.chatservice.exception.UserException;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.channel.ChannelCrudRepository;
import webrtc.chatservice.repository.channel.ChannelListRepository;
import webrtc.chatservice.repository.hashtag.HashTagRepository;
import webrtc.chatservice.repository.users.UsersRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static webrtc.chatservice.enums.ChannelType.TEXT;

@ExtendWith(MockitoExtension.class)
public class ChannelFindServiceTest {

    @InjectMocks
    private ChannelFindServiceImpl channelFindService;

    @Mock
    private ChannelListRepository channelListRepository;
    @Mock
    private ChannelCrudRepository channelCrudRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private HashTagRepository hashTagRepository;

    @Mock
    private ChannelInfoInjectService channelInfoInjectService;

    String nickname1 = "nickname1";
    String password = "password";
    String email1 = "email1";
    String channelName1 = "channelName1";
    String tag1 = "tagname";

    ChannelType text = TEXT;

    int mini = 14;
    int maxi = 20;

    @Test
    void 채널찾기성공() {
        // given
        Channel channel = createChannel(channelName1, text);

        doReturn(Optional.of(channel))
                .when(channelCrudRepository).findById(any(String.class));
        doReturn(channel)
                .when(channelInfoInjectService).setChannelTTL(any(Channel.class));

        // when
        Channel findChannel = channelFindService.findOneChannelById(channel.getId());

        // then
        assertThat(channel.getId()).isEqualTo(findChannel.getId());
    }

    @Test
    void 채널찾기실패채널없음() {
        // given
        Channel channel = createChannel(channelName1, text);

        doReturn(Optional.empty())
                .when(channelCrudRepository).findById(any(String.class));

        // when

        // then
        assertThrows(NotExistChannelException.class, () -> channelFindService.findOneChannelById(channel.getId()));
    }

    @Test
    void 모든채널목록성공20개미만() {
        // given
        doReturn(channelListUnder20())
                .when(channelListRepository).findAnyChannels(any(Integer.class), any(String.class));
        doReturn(channelResponseList(channelListUnder20()))
                .when(channelInfoInjectService).setReturnChannelsTTL(any(ArrayList.class));
        // when
        List<ChannelResponse> result = channelFindService.findAnyChannel("partiASC", 0);

        // then
        assertThat(result.size()).isEqualTo(mini);
    }

    @Test
    void 모든채널목록성공20개이상() {
        // given
        doReturn(channelList20())
                .when(channelListRepository).findAnyChannels(any(Integer.class), any(String.class));
        doReturn(channelResponseList(channelList20()))
                .when(channelInfoInjectService).setReturnChannelsTTL(any(ArrayList.class));
        // when
        List<ChannelResponse> result = channelFindService.findAnyChannel("partiASC", 0);

        // then
        assertThat(result.size()).isEqualTo(maxi);

    }

    @Test
    void 나의채널목록성공20개미만() {
        // given
        doReturn(Optional.of(createUser()))
                .when(usersRepository).findUserByEmail(any(String.class));

        doReturn(channelListUnder20())
                .when(channelListRepository).findMyChannels(any(String.class), any(Integer.class), any(String.class));
        doReturn(channelResponseList(channelListUnder20()))
                .when(channelInfoInjectService).setReturnChannelsTTL(any(ArrayList.class));
        // when
        List<ChannelResponse> result = channelFindService.findMyChannel("partiASC", email1, 0);

        // then
        assertThat(result.size()).isEqualTo(mini);

    }

    @Test
    void 나의채널목록성공20개이상() {
        // given
        doReturn(Optional.of(createUser()))
                .when(usersRepository).findUserByEmail(any(String.class));
        doReturn(channelList20())
                .when(channelListRepository).findMyChannels(any(String.class), any(Integer.class), any(String.class));
        doReturn(channelResponseList(channelList20()))
                .when(channelInfoInjectService).setReturnChannelsTTL(any(ArrayList.class));
        // when
        List<ChannelResponse> result = channelFindService.findMyChannel("partiASC", email1, 0);

        // then
        assertThat(result.size()).isEqualTo(maxi);
    }

    @Test
    void 나의채널목록실패유저없음() {
        // given
        doReturn(Optional.empty())
                .when(usersRepository).findUserByEmail(any(String.class));

        // when


        // then
        assertThrows(NotExistUserException.class, () -> channelFindService.findMyChannel("partiASC", email1, 0));
    }

    @Test
    void 해시태그채널목록성공20개미만() {
        // given
        doReturn(Optional.of(createHashTag()))
                .when(hashTagRepository).findHashTagByName(any(String.class));
        doReturn(channelListUnder20())
                .when(channelListRepository).findChannelsByHashName(any(HashTag.class), any(Integer.class), any(String.class));
        doReturn(channelResponseList(channelListUnder20()))
                .when(channelInfoInjectService).setReturnChannelsTTL(any(ArrayList.class));
        // when
        List<ChannelResponse> result = channelFindService.findChannelByHashName(tag1, "partiASC", 0);

        // then
        assertThat(result.size()).isEqualTo(mini);
    }

    @Test
    void 해시태그채널목록성공20개이상() {
        // given
        doReturn(Optional.of(createHashTag()))
                .when(hashTagRepository).findHashTagByName(any(String.class));
        doReturn(channelList20())
                .when(channelListRepository).findChannelsByHashName(any(HashTag.class), any(Integer.class), any(String.class));
        doReturn(channelResponseList(channelList20()))
                .when(channelInfoInjectService).setReturnChannelsTTL(any(ArrayList.class));
        // when
        List<ChannelResponse> result = channelFindService.findChannelByHashName(tag1, "partiASC", 0);

        // then
        assertThat(result.size()).isEqualTo(maxi);

    }

    @Test
    void 해시태그채널목록실패태그없음() {
        // given
        doReturn(Optional.empty())
                .when(hashTagRepository).findHashTagByName(any(String.class));

        // when

        // then
        assertThrows(NotExistHashTagException.class, () -> channelFindService.findChannelByHashName(tag1, "partiASC", 0));

    }

    private Users createUser() {
        return new Users(nickname1, password, email1);
    }

    private Channel createChannel(String name, ChannelType type) {
        return new Channel(name, type);
    }

    private List<Channel> channelListUnder20() {
        List<Channel> channels = new ArrayList<>();
        for(int i=0; i<mini; i++) {
            Channel channel = new Channel(i + " Channel", text);
            channels.add(channel);
        }
        return channels;
    }

    private List<Channel> channelList20() {
        List<Channel> channels = new ArrayList<>();
        for(int i=0; i<maxi; i++) {
            Channel channel = new Channel(i + " Channel", text);
            channels.add(channel);
        }
        return channels;
    }

    private List<ChannelResponse> channelResponseList(List<Channel> channels) {
        return channels.stream()
                .map(channel -> new ChannelResponse(channel.getId(), channel.getChannelName(), channel.getLimitParticipants(), channel.getCurrentParticipants(), channel.getTimeToLive(), channel.getChannelHashTags
                        (), channel.getChannelType()))
                .collect(Collectors.toList());
    }

    private HashTag createHashTag() {
        return new HashTag(tag1);
    }
}
