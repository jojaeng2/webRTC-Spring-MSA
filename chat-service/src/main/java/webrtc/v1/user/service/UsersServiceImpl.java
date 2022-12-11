package webrtc.v1.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.point.entity.Point;
import webrtc.v1.user.dto.UsersDto.CreateUserRequest;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.user.repository.UsersRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository userRepository;
    private final PasswordEncoder bcryptEncoder;

    @Transactional
    public Users save(CreateUserRequest request) {
        Users user = userBuilder(request);
        Point point = Point.welcomePoint();
        user.addPoint(point);
        userRepository.save(user);
        return user;
    }

    @Transactional(readOnly = true)
    public Users findOneById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(NotExistUserException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Users findOneByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(NotExistUserException::new);
    }

    private Users userBuilder(CreateUserRequest request) {
        return Users.builder()
                .nickname(request.getNickname())
                .password(passwordConverter(request.getPassword()))
                .email(request.getEmail())
                .build();
    }

    private String passwordConverter(String password) {
        return bcryptEncoder.encode(password);
    }
}
