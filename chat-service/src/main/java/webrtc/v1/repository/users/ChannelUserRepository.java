package webrtc.v1.repository.users;

import org.springframework.data.jpa.repository.JpaRepository;
import webrtc.v1.domain.Channel;
import webrtc.v1.domain.ChannelUser;
import webrtc.v1.domain.Users;

import java.util.List;
import java.util.Optional;

public interface ChannelUserRepository extends JpaRepository<ChannelUser, Long> {

    List<ChannelUser> findByChannel(Channel channel);
    Optional<ChannelUser> findByChannelAndUser(Channel channel, Users user);
}
