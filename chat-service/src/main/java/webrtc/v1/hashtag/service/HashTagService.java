package webrtc.v1.hashtag.service;

import webrtc.v1.hashtag.entity.HashTag;

public interface HashTagService {

    HashTag findHashTagByName(String tagName);
}
