package webrtc.v1.service.hashtag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.domain.HashTag;
import webrtc.v1.exception.HashTagException.NotExistHashTagException;
import webrtc.v1.repository.hashtag.HashTagRepository;

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
