package webrtc.chatservice.service.users;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.UsersDto.CreateUserRequest;
import webrtc.chatservice.repository.users.UsersRepository;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder bcryptEncoder;
    private final HttpApiController httpApiController;

    @Transactional
    public Users saveUser(CreateUserRequest request) {
        Users user = Users.builder()
                        .nickname(request.getNickname())
                        .password(bcryptEncoder.encode(request.getPassword()))
                        .email(request.getEmail())
                        .build();
        usersRepository.save(user);
        return user;
    }

    @Transactional(readOnly = true)
    public Users findOneUserByEmail(String email) {
        return usersRepository.findByEmail(email)
                .orElse(httpApiController.postFindUserByEmail(email));
    }

    @Transactional(readOnly = true)
    public int findUserPointByEmail(String email) {
        return httpApiController.postFindUserWithPointByEmail(email).getPoint();
    }

    @Transactional
    public void redisDataEvict() {

    }


}
