package webrtc.openvidu.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.domain.HashTag;
import webrtc.openvidu.repository.hashtag.HashTagRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashTagServiceImpl implements HashTagService{
    private final HashTagRepository hashTagRepository;

    @Transactional
    public List<HashTag> findHashTagByName(String tagName) {
        return hashTagRepository.findHashTagByName(tagName);
    }
}
