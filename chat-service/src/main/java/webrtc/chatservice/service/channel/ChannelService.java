package webrtc.chatservice.service.channel;

import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.ChannelDto.ChannelResponse;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;

import java.util.List;

public interface ChannelService {

    Channel createChannel(CreateChannelRequest request, String email);

    void enterChannel(Channel channel, String email);

    void exitChannel(String channelId, User user);

    void deleteChannel(String channelId);

    List<ChannelResponse> findAnyChannel(int idx);

    List<ChannelResponse> findMyChannel(String email, int idx);

    Channel findOneChannelById(String channelId);

    List<ChannelResponse> findChannelByHashName(String tagName, int idx);

    void extensionChannelTTL(String channelId, String userEmail, Long requestTTL);


}
