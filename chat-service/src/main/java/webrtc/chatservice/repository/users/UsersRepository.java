package webrtc.chatservice.repository.users;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webrtc.chatservice.domain.Users;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, String> {

    @Query("select u from Users u where u.email = :email")
    Optional<Users> findUserByEmail(@Param("email") String email);

    @Query("select u from Users u join u.channelUsers where channel_id = :channel_id")
    List<Users> findUsersByChannelId(@Param("channel_id") String channel_id);

}
