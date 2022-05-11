package webrtc.openvidu.service.user;

import webrtc.openvidu.domain.User;
import webrtc.openvidu.dto.UserDto.CreateUserRequest;

import java.util.List;

public interface UserService {


    User saveUser(CreateUserRequest request);

    User findOneUserByName(String username);

    List<User> findUsersByChannelId(String channelId);
}
