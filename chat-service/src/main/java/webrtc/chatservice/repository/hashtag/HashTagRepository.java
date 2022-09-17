package webrtc.chatservice.repository.hashtag;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webrtc.chatservice.domain.HashTag;

import java.util.List;
import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {

    @Query("select h from HashTag h where h.tagName = :tagName")
    Optional<HashTag> findHashTagByName(@Param("tagName") String tagName);
}
