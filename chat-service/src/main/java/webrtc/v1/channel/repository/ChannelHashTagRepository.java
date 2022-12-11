package webrtc.v1.channel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webrtc.v1.channel.entity.ChannelHashTag;

public interface ChannelHashTagRepository extends JpaRepository<ChannelHashTag, String> {

}
