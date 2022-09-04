package webrtc.chatservice.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.exception.ChannelException;
import webrtc.chatservice.exception.UserException;
import webrtc.chatservice.repository.channel.ChannelDBRepository;
import webrtc.chatservice.repository.channel.ChannelUserRepository;
import webrtc.chatservice.repository.users.UsersRepository;

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
        Users user;

        // 무조건 users 객체 가져와야함
        try {
            user = usersRepository.findUserByEmail(email);
        } catch (UserException.NotExistUserException e) {
            user = httpApiController.postFindUserByEmail(email);
            usersRepository.saveUser(user);
        }

        Channel channel = channelDBRepository.findChannelById(channelId);

        try {
            channelDBRepository.findChannelsByChannelIdAndUserId(channelId, user.getId());
            throw new ChannelException.AlreadyExistUserInChannelException();
        } catch (ChannelException.NotExistChannelException e) {
            Long limitParticipants = channel.getLimitParticipants();
            Long currentParticipants = channel.getCurrentParticipants();
            if(limitParticipants.equals(currentParticipants)) throw new ChannelException.ChannelParticipantsFullException();
            else {
                createChannelUser(user, channel);
            }
        }
        return channel;
    }

    @Override
    @Transactional
    public void exitChannel(String channelId, String userId) {
        Channel channel = channelDBRepository.findChannelById(channelId);
        ChannelUser channelUser = channelUserRepository.findOneChannelUser(channelId, userId);
        channelDBRepository.exitChannelUserInChannel(channel, channelUser);
    }

    private void createChannelUser(Users user, Channel channel) {
        new ChannelUser(user, channel);
    }
}
