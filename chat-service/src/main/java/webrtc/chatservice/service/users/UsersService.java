package webrtc.chatservice.service.users;

import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.ChannelDto.ExtensionChannelInfoWithUserPointResponse;
import webrtc.chatservice.dto.UsersDto.CreateUserRequest;

public interface UsersService {


    Users saveUser(CreateUserRequest request);

    Users findOneUserByEmail(String email);


    void redisDataEvict();

    ExtensionChannelInfoWithUserPointResponse findUserWithPointByEmail(String channelId, String email);
}