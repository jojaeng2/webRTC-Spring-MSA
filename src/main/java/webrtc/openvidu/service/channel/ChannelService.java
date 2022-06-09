package webrtc.openvidu.service.channel;

import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.dto.ChannelDto;
import webrtc.openvidu.dto.ChannelDto.ChannelResponse;
import webrtc.openvidu.dto.ChannelDto.CreateChannelRequest;

import java.util.List;

public interface ChannelService {

    Channel createChannel(CreateChannelRequest request, String email);

    void enterChannel(Channel channel, String email);

    void exitChannel(String channelId, User user);

    void deleteChannel(String channelId);

    List<ChannelResponse> findAnyChannel(int idx);

    List<ChannelResponse> findMyChannel(String email, int idx);

    Channel findOneChannelById(String channelId);

    List<Channel> findChannelByHashName(String tagName);

    void extensionChannelTTL(String channelId, String userEmail, Long requestTTL);


}
