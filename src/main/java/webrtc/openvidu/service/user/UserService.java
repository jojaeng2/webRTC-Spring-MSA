package webrtc.openvidu.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.dto.UserDto.CreateUserRequest;
import webrtc.openvidu.exception.UserException.NotExistUserException;
import webrtc.openvidu.repository.user.UserRepositoryImpl;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepositoryImpl userRepositoryImpl;
    private final PasswordEncoder bcryptEncoder;

    public User saveUser(CreateUserRequest request) {
        User user = new User(request.getNickname(), bcryptEncoder.encode(request.getPassword()));
        userRepositoryImpl.saveUser(user);
        return user;
    }

    public User findOneUserByName(String username) {
        List<User> users = userRepositoryImpl.findUsersByName(username);
        if(users.isEmpty()) throw new NotExistUserException();
        return users.get(0);
    }

    public List<User> findUsersByChannelId(String channelId) {
        return userRepositoryImpl.findUsersByChannelId(channelId);
    }
}
