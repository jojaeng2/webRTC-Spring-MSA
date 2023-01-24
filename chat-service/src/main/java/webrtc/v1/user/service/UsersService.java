package webrtc.v1.user.service;


import webrtc.v1.user.UsersDto.CreateUserRequest;
import webrtc.v1.user.entity.Users;

public interface UsersService {

  // t
  Users save(CreateUserRequest request);

  Users findOneById(String userId);

  // t
  Users findOneByEmail(String email);
}
