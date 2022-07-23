package webrtc.authservice.repository.user;

import webrtc.authservice.domain.User;

public interface UserRepository {

    void save(User user);

    User findUserByEmail(String email);
}
