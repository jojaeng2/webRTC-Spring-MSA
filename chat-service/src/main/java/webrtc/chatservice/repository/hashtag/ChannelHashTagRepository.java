package webrtc.chatservice.repository.hashtag;

import org.springframework.data.jpa.repository.JpaRepository;
import webrtc.chatservice.domain.ChannelHashTag;

public interface ChannelHashTagRepository extends JpaRepository<ChannelHashTag, String> {

}
