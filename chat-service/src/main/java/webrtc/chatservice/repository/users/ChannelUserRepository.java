package webrtc.chatservice.repository.users;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import webrtc.chatservice.domain.ChannelUser;

import java.util.Optional;

public interface ChannelUserRepository extends JpaRepository<ChannelUser, Long> {

    @Query("select cu from ChannelUser cu where channel_id = :channel_id and user_id = :user_id")
    Optional<ChannelUser> findOneChannelUser(@Param("channel_id") String channel_id, @Param("user_id")String user_id);
}
