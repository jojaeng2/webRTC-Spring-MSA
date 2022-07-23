package webrtc.authservice.service.user;

import webrtc.authservice.domain.User;
import webrtc.authservice.dto.UserDto;
import webrtc.authservice.dto.UserDto.CreateUserRequest;
import webrtc.authservice.dto.UserDto.FindUserWithPointByEmailResponse;

public interface UserService {

    User saveUser(CreateUserRequest request);

    User findOneUserByEmail(String email);

    FindUserWithPointByEmailResponse findOneUserWithPointByEmail(String email);


    void decreasePoint(String email, Long point);

    void redisDataEvict();
}
