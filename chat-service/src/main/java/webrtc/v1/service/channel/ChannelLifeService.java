package webrtc.v1.service.channel;

import webrtc.v1.domain.Channel;
import webrtc.v1.dto.ChannelDto.CreateChannelRequest;

public interface ChannelLifeService {

    Channel createChannel(CreateChannelRequest request, String email);

    void deleteChannel(String channelId);

    Channel extensionChannelTTL(String channelId, String userEmail, Long requestTTL);

}
