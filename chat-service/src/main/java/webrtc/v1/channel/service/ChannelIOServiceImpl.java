package webrtc.v1.channel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.channel.entity.ChannelUser;
import webrtc.v1.channel.exception.ChannelException.AlreadyExistUserInChannelException;
import webrtc.v1.channel.exception.ChannelException.ChannelParticipantsFullException;
import webrtc.v1.channel.exception.ChannelException.NotExistChannelException;
import webrtc.v1.channel.exception.ChannelUserException.NotExistChannelUserException;
import webrtc.v1.channel.repository.ChannelCrudRepository;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.user.repository.ChannelUserRepository;
import webrtc.v1.user.repository.UsersRepository;

@Service
@RequiredArgsConstructor
public class ChannelIOServiceImpl implements ChannelIOService {

    private final ChannelCrudRepository channelCrudRepository;
    private final UsersRepository usersRepository;
    private final ChannelUserRepository channelUserRepository;

    @Override
    @Transactional
    public void enterChannel(String channelId, String userId) {
        Users user = findUserById(userId);
        Channel channel = findChannelById(channelId);
        createChannelUser(user, channel);
    }

    @Override
    @Transactional
    public void exitChannel(String channelId, String userId) {
        Users user = findUserById(userId);
        Channel channel = findChannelById(channelId);
        deleteChannelUser(user, channel);
    }

    private void createChannelUser(Users user, Channel channel) {
        isAlreadyExistChannelUser(user, channel);
        if (channel.isFull()) {
            throw new ChannelParticipantsFullException();
        }
        ChannelUser channelUser = channelUserBuilder(user, channel);
        channel.enterChannelUser(channelUser);
        channelUserRepository.save(channelUser);
    }

    private void isAlreadyExistChannelUser(Users user, Channel channel) {
        channelUserRepository.findByChannelAndUser(channel, user)
                .ifPresent(it -> {
                    throw new AlreadyExistUserInChannelException();
                });
    }

    private void deleteChannelUser(Users user, Channel channel) {
        ChannelUser channelUser = findChannelUserByChannelAndUser(channel, user);
        channel.exitChannelUser(channelUser);
        channelUserRepository.delete(channelUser);
    }

    private ChannelUser channelUserBuilder(Users user, Channel channel) {
        return ChannelUser.builder()
                .user(user)
                .channel(channel)
                .build();
    }

    private Users findUserById(String id) {
        return usersRepository.findById(id)
                .orElseThrow(NotExistUserException::new);
    }

    private Channel findChannelById(String id) {
        return channelCrudRepository.findById(id)
                .orElseThrow(NotExistChannelException::new);
    }

    private ChannelUser findChannelUserByChannelAndUser(Channel channel, Users user) {
        return channelUserRepository.findByChannelAndUser(channel, user)
                .orElseThrow(NotExistChannelUserException::new);
    }
}
