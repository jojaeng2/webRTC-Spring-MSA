package webrtc.chatservice.repository.users;

import org.springframework.data.jpa.repository.JpaRepository;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.Users;

import java.util.List;
import java.util.Optional;

public interface ChannelUserRepository extends JpaRepository<ChannelUser, Long> {

    List<ChannelUser> findByChannel(Channel channel);
    Optional<ChannelUser> findByChannelAndUser(Channel channel, Users user);
}
