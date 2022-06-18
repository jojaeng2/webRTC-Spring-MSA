package webrtc.voiceservice.repository.user;

import webrtc.voiceservice.domain.User;

import java.util.List;

public interface UserRepository {

    List<User> findUsersByEmail(String email);

}
