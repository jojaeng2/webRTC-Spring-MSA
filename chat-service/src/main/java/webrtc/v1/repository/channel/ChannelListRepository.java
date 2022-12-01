package webrtc.v1.repository.channel;

import webrtc.v1.domain.Channel;
import webrtc.v1.domain.HashTag;

import java.util.List;
import java.util.UUID;

public interface ChannelListRepository {

    List<Channel> findAnyChannels(int idx, String type);
    List<Channel> findMyChannels(UUID userId, int idx, String type);
    List<Channel> findChannelsByHashName(HashTag hashTag, int idx, String type);

    List<Channel> findChannelsRecentlyTalk(int idx, String type);
}