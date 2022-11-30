package webrtc.v1.service.channel;

import webrtc.v1.domain.Channel;

import java.util.List;

public interface ChannelFindService {

    List<Channel> findAnyChannel(String orderType, int idx);

    List<Channel> findMyChannel(String orderType, String email, int idx);


    Channel findOneChannelById(String channelId);

    List<Channel> findChannelByHashName(String tagName, String orderType, int idx);

    List<Channel> findChannelsRecentlyTalk(String orderType, int idx);
}
