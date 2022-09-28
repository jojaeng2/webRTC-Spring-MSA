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

    public ChannelResponse setReturnChannelsTTL(Channel channel) {
        setChannelTTL(channel);
        return new ChannelResponse(channel);
    }

    public Channel setChannelTTL(Channel channel) {
        Long ttl = channelRedisRepository.findChannelTTL(channel.getId());
        channel.setTimeToLive(ttl);
        return channel;
    }
}
