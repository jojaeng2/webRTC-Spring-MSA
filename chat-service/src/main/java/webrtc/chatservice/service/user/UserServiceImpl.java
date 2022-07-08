package webrtc.chatservice.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.UserDto;
import webrtc.chatservice.dto.UserDto.CreateUserRequest;
import webrtc.chatservice.dto.UserDto.FindUserByEmailRequest;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.utils.CustomJsonMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder bcryptEncoder;
    private final HttpApiController httpApiController;
    private final CustomJsonMapper customJsonMapper;

    @Transactional
    public User saveUser(CreateUserRequest request) {
        User user = new User(request.getNickname(), bcryptEncoder.encode(request.getPassword()), request.getEmail());
        userRepository.saveUser(user);
        return user;
    }

    @Transactional
    public User findOneUserByEmail(String email) {
        return httpApiController.postFindUserByEmail(email);
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
