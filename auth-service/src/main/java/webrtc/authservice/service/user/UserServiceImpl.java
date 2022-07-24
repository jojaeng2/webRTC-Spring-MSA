package webrtc.authservice.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.authservice.domain.Point;
import webrtc.authservice.domain.User;
import webrtc.authservice.dto.UserDto.CreateUserRequest;
import webrtc.authservice.dto.UserDto.FindUserWithPointByEmailResponse;
import webrtc.authservice.exception.UserException.InsufficientPointException;
import webrtc.authservice.repository.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder bcryptEncoder;
    private static int welcomePoint = 1000000;

    @Transactional
    public User save(CreateUserRequest request) {
        User user = new User(request.getNickname(), bcryptEncoder.encode(request.getPassword()), request.getEmail());
        Point point = new Point("회원 가입", welcomePoint);
        user.getPoints().add(point);
        userRepository.save(user);
        return user;
    }

    @Transactional(readOnly = true)
    public User findOneUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
    
    @Transactional(readOnly = true)
    public FindUserWithPointByEmailResponse findOneUserWithPointByEmail(String email) {
        User user = userRepository.findUserByEmail(email);
        List<Point> points = user.getPoints();
        return new FindUserWithPointByEmailResponse(user.getId(), user.getEmail(), user.getNickname(), user.sumOfPoint(points));
    }

    @Transactional
    public void decreasePoint(String email, int amount) {
        User user = userRepository.findUserByEmail(email);
        List<Point> points = user.getPoints();
        int sum = user.sumOfPoint(points);
        if(sum < amount) {
            throw new InsufficientPointException();
        }
        Point point = new Point("채널 연장에 포인트를 사용합니다", -amount);
        user.addPoint(point);
    }

}
