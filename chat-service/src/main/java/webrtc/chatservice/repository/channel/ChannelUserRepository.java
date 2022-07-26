package webrtc.chatservice.repository.channel;

import webrtc.chatservice.domain.ChannelUser;

public interface ChannelUserRepository {

    ChannelUser findOneChannelUser(String channelId, String userId);
}
