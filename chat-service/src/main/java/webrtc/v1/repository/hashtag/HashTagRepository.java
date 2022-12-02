package webrtc.v1.repository.hashtag;

import org.springframework.data.jpa.repository.JpaRepository;
import webrtc.v1.domain.HashTag;

import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag, String> {

    Optional<HashTag> findByTagName(String tagName);
}
