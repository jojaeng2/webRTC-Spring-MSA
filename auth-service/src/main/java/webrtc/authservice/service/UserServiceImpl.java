package webrtc.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.authservice.domain.User;
import webrtc.authservice.dto.UserDto;
import webrtc.authservice.dto.UserDto.CreateUserRequest;
import webrtc.authservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder bcryptEncoder;

    @Transactional
    public User saveUser(CreateUserRequest request) {
        User user = new User(request.getNickname(), bcryptEncoder.encode(request.getPassword()), request.getEmail());
        userRepository.saveUser(user);
        return user;
    }

    @Transactional
    @Cacheable(key = "#email", value = "users")
    public User findOneUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void redisDataEvict() {

    }
}
