package webrtc.chatservice.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.exception.ChannelException;
import webrtc.chatservice.exception.ChannelException.AlreadyExistUserInChannelException;
import webrtc.chatservice.exception.ChannelException.ChannelParticipantsFullException;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.repository.channel.ChannelDBRepository;
import webrtc.chatservice.repository.channel.ChannelUserRepository;
import webrtc.chatservice.repository.users.UsersRepository;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChannelIOServiceImpl implements ChannelIOService{

    private final ChannelDBRepository channelDBRepository;
    private final UsersRepository usersRepository;
    private final ChannelUserRepository channelUserRepository;
    private final HttpApiController httpApiController;

    @Override
    @Transactional
    public Channel enterChannel(String channelId, String email) {
        Users user = findUser(email);
        Channel channel = channelDBRepository.findChannelById(channelId);
        createChannelUser(user, channel);
        return channel;
    }

    private Users findUser(String email) {
        try {
            return usersRepository.findUserByEmail(email);
        } catch (NotExistUserException e) {
            Users user = httpApiController.postFindUserByEmail(email);
            usersRepository.saveUser(user);
            return user;
        }
    }

    private void createChannelUser(Users user, Channel channel) {
        try {
            channelDBRepository.findChannelsByChannelIdAndUserId(channel.getId(), user.getId());
            throw new AlreadyExistUserInChannelException();
        } catch (NotExistChannelException e) {
            Long limitParticipants = channel.getLimitParticipants();
            Long currentParticipants = channel.getCurrentParticipants();
            if(limitParticipants.equals(currentParticipants)) throw new ChannelParticipantsFullException();
            else {
                new ChannelUser(user, channel);
            }
        }
    }

    @Override
    @Transactional
    public void exitChannel(String channelId, String userId) {
        Channel channel = channelDBRepository.findChannelById(channelId);
        ChannelUser channelUser = channelUserRepository.findOneChannelUser(channelId, userId);
        channelDBRepository.exitChannelUserInChannel(channel, channelUser);
    }


}
