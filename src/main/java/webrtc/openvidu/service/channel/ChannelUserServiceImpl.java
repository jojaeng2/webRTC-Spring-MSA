package webrtc.openvidu.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webrtc.openvidu.domain.ChannelUser;
import webrtc.openvidu.repository.channel.ChannelUserRepositoryImpl;

@RequiredArgsConstructor
@Service
public class ChannelUserServiceImpl implements ChannelUserService{

    private final ChannelUserRepositoryImpl channelUserRepositoryImpl;

    public void save(ChannelUser channelUser) {
        channelUserRepositoryImpl.save(channelUser);
    }

    public void delete(ChannelUser channelUser) {
        channelUserRepositoryImpl.delete(channelUser);
    }

    public ChannelUser findOneChannelUser(String channelId, String userId) {
        return channelUserRepositoryImpl.findOneChannelUser(channelId, userId);
    }
}
