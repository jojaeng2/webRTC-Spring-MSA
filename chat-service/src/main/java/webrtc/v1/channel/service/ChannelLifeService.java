package webrtc.v1.channel.service;

import webrtc.v1.channel.dto.ChannelDto.CreateChannelDto;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.dto.ChannelDto.CreateChannelRequest;

import java.util.UUID;

public interface ChannelLifeService {

  Channel create(CreateChannelDto createChannelDto);

  void delete(String channelId);

  Channel extension(String channelId, String userId, Long requestTTL);

}
