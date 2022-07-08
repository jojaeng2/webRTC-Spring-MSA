package webrtc.chatservice.service.user;

import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.UserDto.CreateUserRequest;

import java.util.List;

public interface UserService {


    User saveUser(CreateUserRequest request);

    User findOneUserByEmail(String email);

    List<User> findUsersByChannelId(String channelId);

    void setChannelUser(User user, ChannelUser channelUser);

    void redisDataEvict();

}
