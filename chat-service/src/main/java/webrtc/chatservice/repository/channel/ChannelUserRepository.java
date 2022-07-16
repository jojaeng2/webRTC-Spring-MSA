package webrtc.chatservice.repository.channel;

import webrtc.chatservice.domain.ChannelUser;

public interface ChannelUserRepository {

    void save(ChannelUser channelUser);

    void delete(ChannelUser channelUser);

    ChannelUser findOneChannelUser(String channelId, String userId);
}
