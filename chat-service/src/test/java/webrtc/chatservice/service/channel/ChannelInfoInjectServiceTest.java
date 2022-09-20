package webrtc.chatservice.service.channel;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.HashTag;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.ChannelDto;
import webrtc.chatservice.dto.ChannelDto.ChannelResponse;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.repository.channel.ChannelRedisRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static webrtc.chatservice.enums.ChannelType.TEXT;

@ExtendWith(MockitoExtension.class)
public class ChannelInfoInjectServiceTest {

    @InjectMocks
    private ChannelInfoInjectServiceImpl channelInfoInjectService;

    @Mock
    private ChannelRedisRepository channelRedisRepository;

    String nickname1 = "nickname1";
    String password = "password";
    String email1 = "email1";
    String channelName1 = "channelName1";
    String tag1 = "tagname";

    ChannelType text = TEXT;

    int mini = 14;
    int maxi = 20;
    Long ttl = -2L;

    @Test
    void 채널수명설정성공() {
        // given
        Channel channel = createChannel(channelName1, text);
        doReturn(ttl)
                .when(channelRedisRepository).findChannelTTL(any(String.class));

        // when
        Channel result = channelInfoInjectService.setChannelTTL(channel);

        // then
        assertThat(channel.getTimeToLive()).isEqualTo(ttl);
    }

    @Test
    void 채널들수명설정성공() {
        // given
        doReturn(ttl)
                .when(channelRedisRepository).findChannelTTL(any(String.class));

        // when
        List<ChannelResponse> results = channelInfoInjectService.setReturnChannelsTTL(channelList20());

        // then
        assertThat(results.size()).isEqualTo(maxi);
    }

    private Channel createChannel(String name, ChannelType type) {
        return new Channel(name, type);
    }

    private List<Channel> channelList20() {
        List<Channel> channels = new ArrayList<>();
        for(int i=0; i<maxi; i++) {
            Channel channel = new Channel(i + " Channel", text);
            channels.add(channel);
        }
        return channels;
    }
}
