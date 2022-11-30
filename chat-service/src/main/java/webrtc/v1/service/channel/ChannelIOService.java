package webrtc.v1.service.channel;

import java.util.UUID;

public interface ChannelIOService {

    void enterChannel(String channelId, String email);

    void exitChannel(String channelId, UUID userId);
}
