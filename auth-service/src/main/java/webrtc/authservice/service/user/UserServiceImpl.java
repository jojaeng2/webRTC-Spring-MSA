package webrtc.authservice.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.authservice.domain.Point;
import webrtc.authservice.domain.Users;
import webrtc.authservice.dto.UserDto.CreateUserRequest;
import webrtc.authservice.dto.UserDto.FindUserWithPointByEmailResponse;
import webrtc.authservice.exception.UserException;
import webrtc.authservice.exception.UserException.InsufficientPointException;
import webrtc.authservice.exception.UserException.NotExistUserException;
import webrtc.authservice.repository.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder bcryptEncoder;
    private static int welcomePoint = 10000000;

    @Transactional
    public Users save(CreateUserRequest request) {
        Users user = new Users(request.getNickname(), bcryptEncoder.encode(request.getPassword()), request.getEmail());
        Point point = Point.builder()
                .message("회원 가입")
                .amount(welcomePoint)
                .build();
        user.addPoint(point);
        userRepository.save(user);
        return user;
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "#email", value = "users")
    public Users findOneUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(NotExistUserException::new);
    }
    
    @Transactional(readOnly = true)
    public FindUserWithPointByEmailResponse findOneUserWithPointByEmail(String email) {
        Users user = userRepository.findUserByEmail(email).orElseThrow(NotExistUserException::new);
        return new FindUserWithPointByEmailResponse(user);
    }

    @Transactional
    public void decreasePoint(String email, int amount, String message) {
        Users user = userRepository.findUserByEmail(email).orElseThrow(NotExistUserException::new);

        int sum = user.sumOfPoint(user.getPoints());

        // 포인트 부족
        if(sum < amount) throw new InsufficientPointException();

        Point point = Point.builder()
                .message(message)
                .amount(-amount)
                .build();
        user.addPoint(point);
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void redisDataEvict() {

    }

}
