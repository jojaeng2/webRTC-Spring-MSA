package webrtc.chatservice.service.channel;

import webrtc.chatservice.domain.HashTag;

import java.util.List;

public interface HashTagService {

    List<HashTag> findHashTagByName(String tagName);
}
