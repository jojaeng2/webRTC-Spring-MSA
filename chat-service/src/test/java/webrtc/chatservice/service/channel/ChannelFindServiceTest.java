package webrtc.chatservice.service.channel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.dto.ChannelDto.ChannelResponse;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.repository.channel.ChannelCrudRepository;
import webrtc.chatservice.repository.channel.ChannelListRepository;

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
    private ChannelInfoInjectService channelInfoInjectService;

    String channelName1 = "channelName1";
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
    void 나의채널목록성공20개이상() {
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
    void 해시태그채널목록성공20개미만() {
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
    void 해시태그채널목록성공20개이상() {
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
}
