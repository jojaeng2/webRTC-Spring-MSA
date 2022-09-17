package webrtc.chatservice.repository.channel;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import webrtc.chatservice.domain.Channel;

@Repository
public interface ChannelCrudRepository extends CrudRepository<Channel, String> {

}
