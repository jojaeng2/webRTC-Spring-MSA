package webrtc.v1.channel.service;

import webrtc.v1.channel.dto.ChannelDto.FindChannelByHashTagDto;
import webrtc.v1.channel.dto.ChannelDto.FindChannelDto;
import webrtc.v1.channel.dto.ChannelDto.FindMyChannelDto;
import webrtc.v1.channel.entity.Channel;

import java.util.List;

public interface ChannelFindService {

  List<Channel> findAnyChannel(FindChannelDto request);

  List<Channel> findMyChannel(FindMyChannelDto request);


  Channel findById(String id);

  List<Channel> findByHashName(FindChannelByHashTagDto request);

  List<Channel> findChannelsRecentlyTalk(FindChannelDto request);
}
