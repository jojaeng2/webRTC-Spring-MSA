package webrtc.chatservice.repository.users;

import webrtc.chatservice.domain.Users;

import java.util.List;

public interface UsersRepository {

    void saveUser(Users users);

    Users findUserByEmail(String email);

    List<Users> findUsersByChannelId(String channelId);

}
