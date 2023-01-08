package webrtc.v1.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import webrtc.v1.user.entity.Users;

public interface UsersRepository extends JpaRepository<Users, String> {

  Optional<Users> findByEmail(String email);
}
