package webrtc.v1.service.channel;

import webrtc.v1.domain.Channel;

public interface ChannelInfoInjectService {

    Channel setTtl(Channel channel);
    long findTtl(String id);
}
