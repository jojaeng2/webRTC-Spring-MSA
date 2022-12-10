package webrtc.v1.channel.service;

import webrtc.v1.channel.entity.Channel;

import java.util.List;

public interface ChannelFindService {

    List<Channel> findAnyChannel(String orderType, int idx);

    List<Channel> findMyChannel(String orderType, String userId, int idx);


    Channel findById(String id);

    List<Channel> findByName(String tagName, String orderType, int idx);

    List<Channel> findChannelsRecentlyTalk(String orderType, int idx);
}
