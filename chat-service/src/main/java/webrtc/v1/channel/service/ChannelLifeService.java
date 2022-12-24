package webrtc.v1.channel.service;

import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.dto.ChannelDto.CreateChannelRequest;

import java.util.UUID;

public interface ChannelLifeService {

    Channel create(CreateChannelRequest request, String userId);

    void delete(String channelId);

    Channel extension(String channelId, String userId, Long requestTTL);

}
