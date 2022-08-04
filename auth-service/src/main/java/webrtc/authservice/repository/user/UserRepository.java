package webrtc.authservice.repository.user;

import webrtc.authservice.domain.Users;

public interface UserRepository {

    void save(Users user);

    Users findUserByEmail(String email);
}
