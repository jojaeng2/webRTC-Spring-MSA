package webrtc.v1.hashtag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webrtc.v1.hashtag.entity.HashTag;

import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag, String> {

    Optional<HashTag> findByName(String name);
}
