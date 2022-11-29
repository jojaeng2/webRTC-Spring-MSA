package webrtc.chatservice.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Point;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.UsersDto.CreateUserRequest;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.users.UsersRepository;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository userRepository;
    private final PasswordEncoder bcryptEncoder;
    private int welcomePoint = 10000000;
    private final String joinMessage = "회원 가입";

    @Transactional
    public Users save(CreateUserRequest request) {
        Users user = createUsers(request);
        Point point = createJoinPoint();
        user.addPoint(point);
        userRepository.save(user);
        return user;
    }

    @Transactional(readOnly = true)
    public Users findOneByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(NotExistUserException::new);
    }

    @Transactional(readOnly = true)
    public int findUserPointByEmail(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(NotExistUserException::new);
        return user.sumOfPoint();
    }

    private Users createUsers(CreateUserRequest request) {
        return Users.builder()
                .nickname(request.getNickname())
                .password(passwordConverter(request.getPassword()))
                .email(request.getEmail())
                .build();
    }

    private Point createJoinPoint() {
        return Point.builder()
                .message(joinMessage)
                .amount(welcomePoint)
                .build();
    }

    private String passwordConverter(String password) {
        return bcryptEncoder.encode(password);
    }
}
