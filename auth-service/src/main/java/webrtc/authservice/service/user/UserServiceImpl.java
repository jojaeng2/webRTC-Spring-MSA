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
    
    @Transactional
    public FindUserWithPointByEmailResponse findOneUserWithPointByEmail(String email) {
        User user = userRepository.findUserByEmail(email);
        List<Point> points = user.getPoints();
        return new FindUserWithPointByEmailResponse(user.getId(), user.getEmail(), user.getNickname(), sumOfPoint(points));
    }

    @Transactional
    public void decreasePoint(String email, Long point) {
        User user = userRepository.findUserByEmail(email);
        Point userPoint = user.getPoint();
        if(userPoint.getPoint() < point) {
            throw new InsufficientPointException();
        }
        userPoint.setPoint(userPoint.getPoint() - point);
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
