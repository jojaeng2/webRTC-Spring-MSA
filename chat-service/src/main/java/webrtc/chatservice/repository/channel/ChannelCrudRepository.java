package webrtc.chatservice.repository.channel;

import org.springframework.data.jpa.repository.JpaRepository;
import webrtc.chatservice.domain.Channel;

public interface ChannelCrudRepository extends JpaRepository<Channel, String> {

}
