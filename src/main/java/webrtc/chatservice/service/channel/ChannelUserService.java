package webrtc.chatservice.service.channel;

import webrtc.chatservice.domain.ChannelUser;

public interface ChannelUserService {

    void save(ChannelUser channelUser);

    void delete(ChannelUser channelUser);

    ChannelUser findOneChannelUser(String channelId, String userId);
}
