package webrtc.v1.channel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.entity.ChannelUser;
import webrtc.v1.user.entity.Users;

import java.util.List;
import java.util.Optional;

public interface ChannelUserRepository extends JpaRepository<ChannelUser, Long> {

  List<ChannelUser> findByChannel(Channel channel);

  Optional<ChannelUser> findByChannelAndUser(Channel channel, Users user);
}
