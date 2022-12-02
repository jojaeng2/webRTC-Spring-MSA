package webrtc.v1.user.service;


import webrtc.v1.user.entity.Users;
import webrtc.v1.user.dto.UsersDto.CreateUserRequest;

public interface UsersService {
    Users save(CreateUserRequest request);

    Users findOneByEmail(String email);

    int findUserPointByEmail(String email);
}
