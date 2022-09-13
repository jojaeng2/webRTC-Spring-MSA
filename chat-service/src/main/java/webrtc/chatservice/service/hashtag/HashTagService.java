package webrtc.chatservice.service.hashtag;

import webrtc.chatservice.domain.HashTag;

import java.util.List;

public interface HashTagService {

    HashTag findHashTagByName(String tagName);
}
