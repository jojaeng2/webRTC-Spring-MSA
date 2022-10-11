package webrtc.chatservice.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.dto.ChannelDto.ChannelResponse;
import webrtc.chatservice.repository.channel.ChannelRedisRepository;


@Service
@RequiredArgsConstructor
public class ChannelInfoInjectServiceImpl implements ChannelInfoInjectService{

    private final ChannelRedisRepository channelRedisRepository;

    /**
     * redis 저장소에 접근해 채널의 남은 수명 반환
     */
    public Channel setChannelTTL(Channel channel) {
        Long ttl = channelRedisRepository.findChannelTTL(channel.getId());
        channel.setTimeToLive(ttl);
        return channel;
    }
}
