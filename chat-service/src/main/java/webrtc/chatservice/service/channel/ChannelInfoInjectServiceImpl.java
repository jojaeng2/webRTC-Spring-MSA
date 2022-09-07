package webrtc.chatservice.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.dto.ChannelDto;
import webrtc.chatservice.dto.ChannelDto.ChannelResponse;
import webrtc.chatservice.repository.channel.ChannelRedisRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChannelInfoInjectServiceImpl {

    private final ChannelRedisRepository channelRedisRepository;


    public List<ChannelResponse> setReturnChannelsTTL(List<Channel> channels) {

        return channels.stream()
                .map(channel -> {
                    Long ttl = channelRedisRepository.findChannelTTL(channel.getId());
                    channel.setTimeToLive(ttl);
                    return new ChannelResponse(channel.getId(), channel.getChannelName(), channel.getLimitParticipants(), channel.getCurrentParticipants(), channel.getTimeToLive(), channel.getChannelHashTags(), channel.getChannelType());
                })
                .collect(Collectors.toList());
    }
}
