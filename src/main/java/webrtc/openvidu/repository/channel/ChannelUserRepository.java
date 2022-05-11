package webrtc.openvidu.repository.channel;

import webrtc.openvidu.domain.ChannelUser;

public interface ChannelUserRepository {

    void save(ChannelUser channelUser);

    void delete(ChannelUser channelUser);

    ChannelUser findOneChannelUser(String channelId, String userId);
}
