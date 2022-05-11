package webrtc.openvidu.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webrtc.openvidu.domain.HashTag;
import webrtc.openvidu.repository.hashtag.HashTagRepositoryImpl;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashTagServiceImpl implements HashTagService{
    private final HashTagRepositoryImpl hashTagRepositoryImpl;

    public List<HashTag> findHashTagByName(String tagName) {
        return hashTagRepositoryImpl.findHashTagByName(tagName);
    }
}
