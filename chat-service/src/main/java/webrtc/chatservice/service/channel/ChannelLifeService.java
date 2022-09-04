package webrtc.chatservice.service.channel;

import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;

public interface ChannelLifeService {

    Channel createChannel(CreateChannelRequest request, String email);

    void deleteChannel(String channelId);


    void extensionChannelTTL(String channelId, String userEmail, Long requestTTL);

}
