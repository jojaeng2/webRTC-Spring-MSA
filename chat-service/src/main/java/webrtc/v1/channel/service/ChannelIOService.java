package webrtc.v1.channel.service;

public interface ChannelIOService {

  void enterChannel(String channelId, String userId);

  void exitChannel(String channelId, String userId);
}
