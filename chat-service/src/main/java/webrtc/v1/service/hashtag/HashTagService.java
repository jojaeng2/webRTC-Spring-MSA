package webrtc.v1.service.hashtag;

import webrtc.v1.domain.HashTag;

public interface HashTagService {

    HashTag findHashTagByName(String tagName);
}
