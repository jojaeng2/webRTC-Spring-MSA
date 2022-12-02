package webrtc.v1.repository.hashtag;

import org.springframework.data.jpa.repository.JpaRepository;
import webrtc.v1.domain.ChannelHashTag;

public interface ChannelHashTagRepository extends JpaRepository<ChannelHashTag, String> {

}
