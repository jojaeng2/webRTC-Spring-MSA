package webrtc.chatservice.repository.hashtag;

import webrtc.chatservice.domain.HashTag;

import java.util.List;

public interface HashTagRepository {

    void save(HashTag hashTag);

    HashTag findHashTagById(Long id);

    HashTag findHashTagByName(String tagName);
}
