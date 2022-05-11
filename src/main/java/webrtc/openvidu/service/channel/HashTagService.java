package webrtc.openvidu.service.channel;

import webrtc.openvidu.domain.HashTag;

import java.util.List;

public interface HashTagService {

    List<HashTag> findHashTagByName(String tagName);
}
