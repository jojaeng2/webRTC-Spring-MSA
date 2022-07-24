package webrtc.chatservice.service.user;

import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.ChannelDto;
import webrtc.chatservice.dto.ChannelDto.ExtensionChannelInfoWithUserPointResponse;
import webrtc.chatservice.dto.UserDto;
import webrtc.chatservice.dto.UserDto.CreateUserRequest;
import webrtc.chatservice.dto.UserDto.FindUserWithPointByEmailResponse;

import java.util.List;

public interface UserService {


    User saveUser(CreateUserRequest request);

    User findOneUserByEmail(String email);

    List<User> findUsersByChannelId(String channelId);


    void redisDataEvict();

    ExtensionChannelInfoWithUserPointResponse findUserWithPointByEmail(String channelId, String email);
}
