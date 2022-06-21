package webrtc.authservice.repository;

import webrtc.authservice.domain.User;

public interface UserRepository {

    void saveUser(User user);

    User findUserByEmail(String email);
}
