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

    @Transactional
    public User save(CreateUserRequest request) {
        User user = new User(request.getNickname(), bcryptEncoder.encode(request.getPassword()), request.getEmail());
        Point point = new Point("회원 가입", 100);
        user.getPoints().add(point);
        userRepository.save(user);
        return user;
    }

    @Transactional
    @Cacheable(key = "#email", value = "users")
    public User findOneUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
    
    @Transactional
    public FindUserWithPointByEmailResponse findOneUserWithPointByEmail(String email) {
        User user = userRepository.findUserByEmail(email);
        List<Point> points = user.getPoints();
        return new FindUserWithPointByEmailResponse(user.getId(), user.getEmail(), user.getNickname(), sumOfPoint(points));
    }

    @Transactional
    public void decreasePoint(String email, int amount) {
        User user = userRepository.findUserByEmail(email);
        List<Point> points = user.getPoints();
        int psum = sumOfPoint(points);
        if(psum < amount) {
            throw new InsufficientPointException();
        }
        Point point = new Point("채널 연장에 포인트를 사용합니다", -amount);
        user.addPoint(point);
        userRepository.save(user);
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void redisDataEvict() {

    }

    public int sumOfPoint(List<Point> points) {
        int sum = 0;
        for (Point point : points) sum += point.getAmount();
        return sum;
    }
}
