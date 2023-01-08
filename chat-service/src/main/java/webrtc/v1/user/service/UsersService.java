package webrtc.v1.user.service;


import webrtc.v1.user.entity.Users;

public interface UsersService {

  Users findOneById(String userId);
}
