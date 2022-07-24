package webrtc.chatservice.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.ChannelDto;
import webrtc.chatservice.dto.ChannelDto.ExtensionChannelInfoWithUserPointResponse;
import webrtc.chatservice.dto.UserDto;
import webrtc.chatservice.dto.UserDto.CreateUserRequest;
import webrtc.chatservice.dto.UserDto.FindUserByEmailRequest;
import webrtc.chatservice.dto.UserDto.FindUserWithPointByEmailResponse;
import webrtc.chatservice.exception.UserException;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.channel.ChannelRepository;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.utils.CustomJsonMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final ChannelRepository channelRepository;
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
        try {
            return userRepository.findUserByEmail(email);
        }
        catch (NotExistUserException e) {
            return httpApiController.postFindUserByEmail(email);
        }
    }

    @Transactional
    public ExtensionChannelInfoWithUserPointResponse findUserWithPointByEmail(String channelId, String email) {
        FindUserWithPointByEmailResponse response = httpApiController.postFindUserWithPointByEmail(email);
        return new ExtensionChannelInfoWithUserPointResponse(channelRepository.findChannelTTL(channelId), response.getPoint());
    }

    @CacheEvict(value = "users", allEntries = true)
    public void redisDataEvict() {

    }

    @Transactional
    public List<User> findUsersByChannelId(String channelId) {
        return userRepository.findUsersByChannelId(channelId);
    }

}
