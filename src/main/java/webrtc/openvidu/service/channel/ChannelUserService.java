package webrtc.openvidu.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.repository.ChannelUserRepository;

@RequiredArgsConstructor
@Service
public class ChannelUserService {

    private final ChannelUserRepository channelUserRepository;

    public void enterChannel(Channel channel, User user) {
        channelUserRepository.enterChannel(channel, user);
    }

    public void exitChannel(String channelId, String userId) {
        channelUserRepository.exitChannel(channelId, userId);
    }
}
