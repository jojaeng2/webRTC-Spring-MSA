package webrtc.chatservice.repository.hashtag;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webrtc.chatservice.domain.HashTag;

import java.util.List;
import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag, String> {

    Optional<HashTag> findByTagName(String tagName);
}
