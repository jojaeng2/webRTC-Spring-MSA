package webrtc.v1.service.channel;

import webrtc.v1.domain.Channel;
import webrtc.v1.dto.ChannelDto.CreateChannelRequest;

public interface ChannelLifeService {

    Channel create(CreateChannelRequest request, String email);

    void delete(String channelId);

    Channel extension(String channelId, String userEmail, Long requestTTL);

}
