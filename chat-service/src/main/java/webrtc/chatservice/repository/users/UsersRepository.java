package webrtc.chatservice.repository.users;

import webrtc.chatservice.domain.User;

import java.util.List;

public interface UsersRepository {

    void saveUser(User user);

    User findUserByEmail(String email);

    List<User> findUsersByChannelId(String channelId);

}
