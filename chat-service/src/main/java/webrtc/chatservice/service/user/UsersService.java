package webrtc.chatservice.service.user;


import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.UsersDto.CreateUserRequest;

public interface UsersService {
    Users save(CreateUserRequest request);

    Users findOneByEmail(String email);

    int findUserPointByEmail(String email);
}
