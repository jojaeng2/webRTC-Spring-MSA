package webrtc.v1.service.user;


import webrtc.v1.domain.Users;
import webrtc.v1.dto.UsersDto.CreateUserRequest;

public interface UsersService {
    Users save(CreateUserRequest request);

    Users findOneByEmail(String email);

    int findUserPointByEmail(String email);
}
