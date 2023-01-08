package webrtc.v1.hashtag.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import webrtc.v1.hashtag.entity.HashTag;

public interface HashTagRepository extends JpaRepository<HashTag, String> {

  Optional<HashTag> findByName(String name);
}
