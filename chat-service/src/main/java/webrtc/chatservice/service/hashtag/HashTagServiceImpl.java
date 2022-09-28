package webrtc.chatservice.service.hashtag;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.HashTag;
import webrtc.chatservice.exception.HashTagException;
import webrtc.chatservice.exception.HashTagException.NotExistHashTagException;
import webrtc.chatservice.repository.hashtag.HashTagRepository;

@Service
@RequiredArgsConstructor
public class HashTagServiceImpl implements HashTagService{
    private final HashTagRepository hashTagRepository;

    @Transactional(readOnly = true)
    public HashTag findHashTagByName(String tagName) {
        return hashTagRepository.findByTagName(tagName).orElseThrow(NotExistHashTagException::new);
    }
}
