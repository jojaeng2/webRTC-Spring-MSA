package webrtc.openvidu.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webrtc.openvidu.domain.ChannelUser;
import webrtc.openvidu.repository.ChannelUserRepository;

@RequiredArgsConstructor
@Service
public class ChannelUserService {

    private final ChannelUserRepository channelUserRepository;

    public void save(ChannelUser channelUser) {
        channelUserRepository.save(channelUser);
    }

    public void exitChannel(String channelId, String userId) {
        channelUserRepository.exitChannel(channelId, userId);
    }
}
