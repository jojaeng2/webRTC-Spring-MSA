package webrtc.v1.channel.service;

import webrtc.v1.channel.entity.Channel;

public interface ChannelInfoInjectService {

    Channel setTtl(Channel channel);
    long findTtl(String id);
}
