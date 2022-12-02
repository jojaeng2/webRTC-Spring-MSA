package webrtc.v1.service.channel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import webrtc.v1.domain.Channel;
import webrtc.v1.enums.ChannelType;
import webrtc.v1.repository.channel.ChannelRedisRepository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static webrtc.v1.enums.ChannelType.TEXT;

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
    Long ttl = 10L;

    @Test
    void 채널수명설정성공() {
        // given
        Channel channel = createChannel(channelName1, text);
        doReturn(ttl)
                .when(channelRedisRepository).findTtl(any(String.class));

        // when
        Channel result = channelInfoInjectService.setTtl(channel);

        // then
        assertThat(channel.getTimeToLive()).isEqualTo(ttl);
    }


    private Channel createChannel(String name, ChannelType type) {
        return Channel.builder()
                .channelName(name)
                .channelType(type)
                .build();
    }

}
