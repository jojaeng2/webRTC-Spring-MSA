package webrtc.v1.service.channel;

import webrtc.v1.domain.Channel;

public interface ChannelInfoInjectService {

    Channel setChannelTTL(Channel channel);
    long findChannelTTL(String id);
}
