package webrtc.chatservice.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.UserDto.CreateUserRequest;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

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

    @Transactional
    public List<User> findUsersByChannelId(String channelId) {
        return userRepository.findUsersByChannelId(channelId);
    }

    @Transactional
    public void setChannelUser(User user, ChannelUser channelUser) {
        userRepository.setChannelUser(user, channelUser);
    }
}
