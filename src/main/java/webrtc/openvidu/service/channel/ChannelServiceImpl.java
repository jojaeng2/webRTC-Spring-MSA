package webrtc.openvidu.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webrtc.openvidu.domain.ChannelUser;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.dto.ChannelDto.CreateChannelRequest;
import webrtc.openvidu.exception.ChannelException.AlreadyExistChannelException;
import webrtc.openvidu.exception.ChannelException.ChannelParticipantsFullException;
import webrtc.openvidu.exception.ChannelException.NotExistChannelException;
import webrtc.openvidu.repository.channel.ChannelRepository;
import webrtc.openvidu.service.user.UserService;

import java.util.List;


@RequiredArgsConstructor
@Service
public class ChannelServiceImpl implements ChannelService{


    private final ChannelRepository channelRepository;
    private final UserService userService;
    private final ChannelUserService channelUserService;

    /**
     * 비즈니스 로직 - 채널 생성
     * @Param channelName
     * @Param limitParticipants
     *
     * @return Channel
     */
    public Channel createChannel(CreateChannelRequest request, String userName) {
        List<Channel> channels = channelRepository.findChannelsByChannelName(request.getChannelName());
        if(!channels.isEmpty()) {
            throw new AlreadyExistChannelException();
        }

        Channel channel = new Channel(request.getChannelName());
        User user = userService.findOneUserByName(userName);
        List<String> hashTags = request.getHashTags();
        channelRepository.createChannel(channel, hashTags);

        ChannelUser channelUser = new ChannelUser();
        channel.addChannelUser(channelUser);
        user.addChannelUser(channelUser);
        channelUserService.save(channelUser);
        return channel;
    }

    /*
     * 비즈니스 로직 - 채널 입장
     *
     */
    public void enterChannel(Channel channel, String userName) {
        User user = userService.findOneUserByName(userName);
        String channelId = channel.getId();
        List<Channel> findEnterChannels = channelRepository.findChannelsByUserId(channelId, user.getId());

        // !findEnterChannels.isEmpty() -> 이미 해당 user가 채널에 입장한 상태라는 의미
        if(findEnterChannels.isEmpty()) {
            Long limitParticipants = channel.getLimitParticipants();
            Long currentParticipants = channel.getCurrentParticipants();
            if(limitParticipants.equals(currentParticipants)) throw new ChannelParticipantsFullException();
            else {
                ChannelUser channelUser = new ChannelUser();
                channel.addChannelUser(channelUser);
                user.addChannelUser(channelUser);
                System.out.println("channelUser = " + channelUser.getId());
                channelUserService.save(channelUser);
            }
        }
    }

    /*
     * 비즈니스 로직 - 채널 퇴장
     *
     */
    public void exitChannel(String channelId, String userName) {
        User user = userService.findOneUserByName(userName);
        Channel channel = findOneChannelById(channelId);
        ChannelUser channelUser = channelUserService.findOneChannelUser(channelId, user.getId());
        channel.minusCurrentParticipants();
        channelUserService.delete(channelUser);
    }

    /*
     * 비즈니스 로직 - 채널 삭제
     *
     */
    public void deleteChannel(String channelId) {
        Channel channel = findOneChannelById(channelId);
        channelRepository.deleteChannel(channel);
    }


    /*
     * 비즈니스 로직 - 모든 채널 불러오기
     *
     */
    public List<Channel> findAllChannel() {
        List<Channel> channels = channelRepository.findAllChannel();
        for (Channel channel : channels) {
            channel.setTimeToLive(channelRepository.findChannelTTL(channel.getId()));
        }
        return channels;
    }

    /*
     * 비즈니스 로직 - 특정 채널 ID로 찾기
     */
    public Channel findOneChannelById(String channelId) {
        List<Channel> channels = channelRepository.findChannelsById(channelId);
        if(channels.size() == 0) throw new NotExistChannelException();
        Channel findChannel = channels.get(0);
        findChannel.setTimeToLive(channelRepository.findChannelTTL(channelId));
        return findChannel;
    }


    public List<Channel> findChannelByHashName(String tagName) {
        return channelRepository.findChannelsByHashName(tagName);
    }
}
