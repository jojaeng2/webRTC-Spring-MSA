package webrtc.v1.repository.channel;

import org.springframework.data.jpa.repository.JpaRepository;
import webrtc.v1.domain.Channel;

import java.util.Optional;

public interface ChannelCrudRepository extends JpaRepository<Channel, String> {

    Optional<Channel> findByChannelName(String channelName);
}
