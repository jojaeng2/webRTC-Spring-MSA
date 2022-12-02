package webrtc.v1.hashtag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.hashtag.entity.HashTag;
import webrtc.v1.hashtag.exception.HashTagException.NotExistHashTagException;
import webrtc.v1.hashtag.repository.HashTagRepository;

@Service
@RequiredArgsConstructor
public class HashTagServiceImpl implements HashTagService{
    private final HashTagRepository hashTagRepository;

    @Transactional(readOnly = true)
    public HashTag findHashTagByName(String tagName) {
        return hashTagRepository.findByTagName(tagName)
                .orElseThrow(NotExistHashTagException::new);
    }
}
