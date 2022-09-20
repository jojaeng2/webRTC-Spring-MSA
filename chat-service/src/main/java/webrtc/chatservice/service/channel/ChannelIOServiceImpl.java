package webrtc.chatservice.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChannelUser;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.exception.ChannelException.AlreadyExistUserInChannelException;
import webrtc.chatservice.exception.ChannelException.ChannelParticipantsFullException;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.exception.ChannelUserException.NotExistChannelUserException;
import webrtc.chatservice.repository.channel.ChannelCrudRepository;
import webrtc.chatservice.repository.channel.ChannelListRepository;
import webrtc.chatservice.repository.users.ChannelUserRepository;
import webrtc.chatservice.repository.users.UsersRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelIOServiceImpl implements ChannelIOService{

    private final ChannelListRepository channelListRepository;
    private final ChannelCrudRepository channelCrudRepository;
    private final UsersRepository usersRepository;
    private final ChannelUserRepository channelUserRepository;
    private final HttpApiController httpApiController;

    @Override
    @Transactional
    public void enterChannel(String channelId, String email) {
        Users user = findUser(email);
        Channel channel = channelCrudRepository.findById(channelId)
                .orElseThrow(NotExistChannelException::new);
        createChannelUser(user, channel);
    }

    @Override
    @Transactional
    public void exitChannel(String channelId, String userId) {
        Channel channel = channelCrudRepository.findById(channelId).orElseThrow(NotExistChannelException::new);
        exitChannelUserInChannel(channel, userId);
    }

    private Users findUser(String email) {
        Users user = usersRepository.findUserByEmail(email)
                .orElse(httpApiController.postFindUserByEmail(email));
        usersRepository.save(user);
        return user;
    }

    private void createChannelUser(Users user, Channel channel) {

        /*
            channel이랑 Users로 ChannelUsers 조회

            if(!Empty) 채널에 입장한 적이 있음
            else 처음 입장하는 채널 -> {자리 있는지 확인 후 채널에 입장시킴}
         */

        List<Channel> channels = channelListRepository.findChannelsByChannelIdAndUserId(channel.getId(), user.getId());
        if(!channels.isEmpty()) throw new AlreadyExistUserInChannelException();

        Long limitParticipants = channel.getLimitParticipants();
        Long currentParticipants = channel.getCurrentParticipants();
        if(limitParticipants.equals(currentParticipants)) throw new ChannelParticipantsFullException();
        else {
            ChannelUser channelUser = new ChannelUser(user, channel);
            channelUserRepository.save(channelUser);
        }
    }

    private void exitChannelUserInChannel(Channel channel, String userId) {
        ChannelUser channelUser = channelUserRepository.findOneChannelUser(channel.getId(), userId).orElseThrow(NotExistChannelUserException::new);
        channel.exitChannelUser(channelUser);
        channelUserRepository.delete(channelUser);
    }
}
