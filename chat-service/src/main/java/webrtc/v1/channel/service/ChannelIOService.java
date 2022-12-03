package webrtc.v1.channel.service;

import java.util.UUID;

public interface ChannelIOService {

    void enterChannel(String channelId, String email);

    void exitChannel(String channelId, UUID userId);
}