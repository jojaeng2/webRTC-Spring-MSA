package webrtc.openvidu.repository.user;

import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.domain.User;

import java.util.List;

public interface UserRepository {

    void saveUser(User user);

    List<User> findUsersByName(String name);

    List<User> findUsersByChannelId(String channelId);

}