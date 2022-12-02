package webrtc.v1.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.domain.Point;
import webrtc.v1.domain.Users;
import webrtc.v1.dto.UsersDto.CreateUserRequest;
import webrtc.v1.exception.UserException.NotExistUserException;
import webrtc.v1.repository.users.UsersRepository;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository userRepository;
    private final PasswordEncoder bcryptEncoder;
    private int welcomePoint = 10000000;
    private final String joinMessage = "회원 가입";

    @Transactional
    public Users save(CreateUserRequest request) {
        Users user = userBuilder(request);
        Point point = joinPointBuilder();
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

    private Users userBuilder(CreateUserRequest request) {
        return Users.builder()
                .nickname(request.getNickname())
                .password(passwordConverter(request.getPassword()))
                .email(request.getEmail())
                .build();
    }

    private Point joinPointBuilder() {
        return Point.builder()
                .message(joinMessage)
                .amount(welcomePoint)
                .build();
    }

    private String passwordConverter(String password) {
        return bcryptEncoder.encode(password);
    }
}
