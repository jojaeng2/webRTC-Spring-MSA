package webrtc.v1.channel.service;

import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.dto.ChannelDto.CreateChannelRequest;

public interface ChannelLifeService {

    Channel create(CreateChannelRequest request, String email);

    void delete(String channelId);

    Channel extension(String channelId, String userEmail, Long requestTTL);

}
