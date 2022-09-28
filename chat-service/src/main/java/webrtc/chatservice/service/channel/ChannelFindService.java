package webrtc.chatservice.service.channel;

import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.dto.ChannelDto.ChannelResponse;

import java.util.List;

public interface ChannelFindService {

    List<ChannelResponse> findAnyChannel(String orderType, int idx);

    List<ChannelResponse> findMyChannel(String orderType, String email, int idx);


    Channel findOneChannelById(String channelId);

    List<ChannelResponse> findChannelByHashName(String tagName, String orderType, int idx);

    List<ChannelResponse> findChannelsRecentlyTalk(String orderType, int idx);
}
