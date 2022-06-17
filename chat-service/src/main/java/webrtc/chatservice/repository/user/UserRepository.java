package webrtc.chatservice.repository.user;

import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.User;

import java.util.List;

public interface UserRepository {

    void saveUser(User user);

    List<User> findUsersByEmail(String email);

    List<User> findUsersByChannelId(String channelId);

    void setChannelUser(User user, ChannelUser channelUser);

}
