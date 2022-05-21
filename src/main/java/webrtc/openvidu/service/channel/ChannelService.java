package webrtc.openvidu.service.channel;

import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.dto.ChannelDto.CreateChannelRequest;

import java.util.List;

public interface ChannelService {

    Channel createChannel(CreateChannelRequest request, String userName);

    void enterChannel(Channel channel, String userName);

    void exitChannel(String channelId, String userName);

    void deleteChannel(String channelId);

    List<Channel> findAllChannel();

    List<Channel> findMyAllChannel(String userName);

    Channel findOneChannelById(String channelId);

    List<Channel> findChannelByHashName(String tagName);
}
