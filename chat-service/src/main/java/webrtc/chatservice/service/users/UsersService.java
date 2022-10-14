package webrtc.chatservice.service.users;

import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.UsersDto.CreateUserRequest;

public interface UsersService {


    Users saveUser(CreateUserRequest request);

    Users findOneUserByEmail(String email);


    void redisDataEvict();

    int findUserPointByEmail(String email);
}
