package webrtc.v1.channel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webrtc.v1.channel.entity.Channel;

import java.util.Optional;

public interface ChannelCrudRepository extends JpaRepository<Channel, String> {

    Optional<Channel> findByChannelName(String channelName);
}
