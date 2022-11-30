package webrtc.chatservice.repository.users;

import org.springframework.data.jpa.repository.JpaRepository;
import webrtc.chatservice.domain.Users;

import java.util.Optional;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<Users, UUID> {

    Optional<Users> findByEmail(String email);

}
