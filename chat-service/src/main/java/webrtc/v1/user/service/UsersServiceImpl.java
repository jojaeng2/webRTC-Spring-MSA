package webrtc.v1.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.user.dto.UsersDto.CreateUserRequest;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.user.repository.UsersRepository;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

  private final UsersRepository userRepository;

  @Transactional(readOnly = true)
  public Users findOneById(String userId) {
    return userRepository.findById(userId)
        .orElseThrow(NotExistUserException::new);
  }
}
