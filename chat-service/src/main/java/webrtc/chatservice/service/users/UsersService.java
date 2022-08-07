package webrtc.chatservice.service.users;

import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.ChannelDto.ExtensionChannelInfoWithUserPointResponse;
import webrtc.chatservice.dto.UsersDto.CreateUserRequest;

public interface UsersService {


    User saveUser(CreateUserRequest request);

    User findOneUserByEmail(String email);


    void redisDataEvict();

    ExtensionChannelInfoWithUserPointResponse findUserWithPointByEmail(String channelId, String email);
}
