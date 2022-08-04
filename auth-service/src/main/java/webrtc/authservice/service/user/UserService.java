package webrtc.authservice.service.user;

import webrtc.authservice.domain.Users;
import webrtc.authservice.dto.UserDto.CreateUserRequest;
import webrtc.authservice.dto.UserDto.FindUserWithPointByEmailResponse;

public interface UserService {

    Users save(CreateUserRequest request);

    Users findOneUserByEmail(String email);

    FindUserWithPointByEmailResponse findOneUserWithPointByEmail(String email);


    void decreasePoint(String email, int amount);

    void redisDataEvict();
}
