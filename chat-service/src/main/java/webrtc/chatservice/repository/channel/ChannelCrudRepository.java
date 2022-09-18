package webrtc.chatservice.repository.channel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import webrtc.chatservice.domain.Channel;

public interface ChannelCrudRepository extends JpaRepository<Channel, String> {

}
