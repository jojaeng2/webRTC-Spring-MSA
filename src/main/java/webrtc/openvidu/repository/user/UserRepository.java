package webrtc.openvidu.repository.user;

import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.domain.ChannelUser;
import webrtc.openvidu.domain.User;

import java.util.List;

public interface UserRepository {

    void saveUser(User user);

    List<User> findUsersByEmail(String email);

    List<User> findUsersByChannelId(String channelId);

    void setChannelUser(User user, ChannelUser channelUser);

}
