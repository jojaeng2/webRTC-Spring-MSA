package webrtc.v1.channel.service;

import java.util.UUID;

public interface ChannelIOService {

    void enterChannel(String channelId, UUID userId);

    void exitChannel(String channelId, UUID userId);
}
