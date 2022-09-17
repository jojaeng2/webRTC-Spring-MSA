package webrtc.chatservice.service.channel;

import webrtc.chatservice.domain.Channel;

public interface ChannelIOService {

    void enterChannel(String channelId, String email);

    void exitChannel(String channelId, String userId);
}
