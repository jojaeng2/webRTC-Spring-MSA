package webrtc.openvidu.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webrtc.openvidu.domain.ChannelUser;
import webrtc.openvidu.repository.channel.ChannelUserRepository;

@RequiredArgsConstructor
@Service
public class ChannelUserServiceImpl implements ChannelUserService{

    private final ChannelUserRepository channelUserRepository;

    public void save(ChannelUser channelUser) {
        channelUserRepository.save(channelUser);
    }

    public void delete(ChannelUser channelUser) {
        channelUserRepository.delete(channelUser);
    }

    public ChannelUser findOneChannelUser(String channelId, String userId) {
        return channelUserRepository.findOneChannelUser(channelId, userId);
    }
}
