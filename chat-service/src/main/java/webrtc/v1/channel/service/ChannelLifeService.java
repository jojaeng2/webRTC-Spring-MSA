package webrtc.v1.channel.service;

import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.dto.ChannelDto.CreateChannelRequest;

import java.util.UUID;

public interface ChannelLifeService {

    Channel create(CreateChannelRequest request, UUID userId);

    void delete(String channelId);

    Channel extension(String channelId, UUID userId, Long requestTTL);

}
