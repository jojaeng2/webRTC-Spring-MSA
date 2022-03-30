package webrtc.openvidu.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webrtc.openvidu.domain.HashTag;
import webrtc.openvidu.repository.HashTagRepository;

@Service
@RequiredArgsConstructor
public class HashTagService {
    private final HashTagRepository hashTagRepository;

    public HashTag findHashTagByName(String tagName) {
        return findHashTagByName(tagName);
    }
}
