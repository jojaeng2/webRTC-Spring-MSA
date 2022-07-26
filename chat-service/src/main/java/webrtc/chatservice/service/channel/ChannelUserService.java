package webrtc.chatservice.service.channel;

import webrtc.chatservice.domain.ChannelUser;

public interface ChannelUserService {

    ChannelUser findOneChannelUser(String channelId, String userId);
}
