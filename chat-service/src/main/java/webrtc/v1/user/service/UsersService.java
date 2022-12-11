package webrtc.v1.user.service;


import webrtc.v1.user.entity.Users;
import webrtc.v1.user.dto.UsersDto.CreateUserRequest;

import java.util.UUID;

public interface UsersService {
    Users save(CreateUserRequest request);

    Users findOneById(UUID userId);
}
