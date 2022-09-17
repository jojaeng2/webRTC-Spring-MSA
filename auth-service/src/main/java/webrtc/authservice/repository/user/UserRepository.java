package webrtc.authservice.repository.user;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webrtc.authservice.domain.Users;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String>{

    @Query("select u from Users u where u.email = :email")
    Optional<Users> findUserByEmail(@Param("email") String email);
}
