package webrtc.chatservice.repository.channel;

import org.springframework.data.jpa.repository.JpaRepository;
import webrtc.chatservice.domain.ChannelHashTag;

public interface ChannelHashTagRepository extends JpaRepository<ChannelHashTag, Long> {

}
