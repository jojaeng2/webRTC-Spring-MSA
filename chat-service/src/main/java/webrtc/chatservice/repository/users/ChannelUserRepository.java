package webrtc.chatservice.repository.users;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.Users;

import java.util.Optional;

public interface ChannelUserRepository extends JpaRepository<ChannelUser, Long> {

    Optional<ChannelUser> findByChannelAndUser(Channel channel, Users user);
}
