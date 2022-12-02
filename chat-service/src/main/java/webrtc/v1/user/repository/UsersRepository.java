package webrtc.v1.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webrtc.v1.user.entity.Users;

import java.util.Optional;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<Users, UUID> {

    Optional<Users> findByEmail(String email);

}
