package webrtc.chatservice.service.users;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.ChannelDto.ExtensionChannelInfoWithUserPointResponse;
import webrtc.chatservice.dto.UsersDto.CreateUserRequest;
import webrtc.chatservice.dto.UsersDto.FindUserWithPointByEmailResponse;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.channel.ChannelRedisRepository;
import webrtc.chatservice.repository.users.UsersRepository;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {
    private final ChannelRedisRepository channelRedisRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder bcryptEncoder;
    private final HttpApiController httpApiController;

    @Transactional
    public Users saveUser(CreateUserRequest request) {
        Users user = new Users(request.getNickname(), bcryptEncoder.encode(request.getPassword()), request.getEmail());
        usersRepository.saveUser(user);
        return user;
    }

    @Transactional(readOnly = true)
    public Users findOneUserByEmail(String email) {
        try {
            return usersRepository.findUserByEmail(email);
        }
        catch (NotExistUserException e) {
            return httpApiController.postFindUserByEmail(email);
        }
    }

    @Transactional(readOnly = true)
    public ExtensionChannelInfoWithUserPointResponse findUserWithPointByEmail(String channelId, String email) {
        FindUserWithPointByEmailResponse response = httpApiController.postFindUserWithPointByEmail(email);
        Long ttl = channelRedisRepository.findChannelTTL(channelId);
        if(ttl == -2) throw new NotExistChannelException();
        return new ExtensionChannelInfoWithUserPointResponse(ttl, response.getPoint());
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void redisDataEvict() {

    }


}
