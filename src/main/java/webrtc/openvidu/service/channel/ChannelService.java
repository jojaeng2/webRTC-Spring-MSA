package webrtc.openvidu.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.DispatcherServlet;
import webrtc.openvidu.domain.ChannelUser;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.dto.ChannelDto.CreateChannelRequest;
import webrtc.openvidu.enums.ChannelServiceReturnType;
import webrtc.openvidu.exception.ChannelException;
import webrtc.openvidu.exception.ChannelException.AlreadyExistChannelException;
import webrtc.openvidu.exception.ChannelException.NotExistChannelException;
import webrtc.openvidu.repository.ChannelHashTagRepository;
import webrtc.openvidu.repository.ChannelRepository;
import webrtc.openvidu.repository.ChannelUserRepository;
import webrtc.openvidu.repository.UserRepository;

import java.util.List;

import static webrtc.openvidu.enums.ChannelServiceReturnType.*;

@RequiredArgsConstructor
@Service
public class ChannelService {


    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ChannelHashTagRepository channelHashTagRepository;
    private final ChannelUserRepository channelUserRepository;
    private final Long limitParticipants = 15L;

    /**
     * 비즈니스 로직 - 채널 생성
     * @Param channelName
     * @Param limitParticipants
     *
     * @return Channel
     */
    public Channel createChannel(CreateChannelRequest request) {
        List<Channel> channels = channelRepository.findOneChannelByChannelName(request.getChannelName());
        if(!channels.isEmpty()) {
            throw new AlreadyExistChannelException();
        }
        return channelRepository.createChannel(request);
    }

    /*
     * 비즈니스 로직 - 채널 입장
     *
     */
    public ChannelServiceReturnType enterChannel(String channelId, String userName) {
        List<User> users = userRepository.findUserByName(userName);
        List<Channel> findEnterChannels = channelRepository.findChannelsByUserId(channelId, users.get(0).getId());
        if(!findEnterChannels.isEmpty()) {
            return EXIST;
        }
        else {
            User user = users.get(0);
            Channel channel = findOneChannelById(channelId);
            System.out.println("channelId = " + channelId);
            System.out.println("channel.getId() = " + channel.getId());
            Long limitParticipants = channel.getLimitParticipants();
            Long currentParticipants = channel.getCurrentParticipants();
            if(limitParticipants.equals(currentParticipants)) {
                return FULLCHANNEL;
            }
            else {
                channelUserRepository.enterChannel(channel, user);

                channelRepository.updateChannel(channel);
                return SUCCESS;
            }
        }
    }

    /*
     * 비즈니스 로직 - 채널 퇴장
     *
     */
    public void exitChannel(String channelId, String userName) {
        List<User> users = userRepository.findUserByName(userName);
        User user = users.get(0);
        channelUserRepository.exitChannel(channelId, user.getId());
    }

    /*
     * 비즈니스 로직 - 채널 삭제
     *
     */
    public void deleteChannel() {

    }

    /*
     * 비즈니스 로직 - 채널 업데이트
     *
     * 채널 인원 추가, 인원 삭제, 남은 시간 등 업데이트
     */
//    public void updateChannel(Channel channel) {
//        channelRepository.updateChannel(channel);
//    }

    /*
     * 비즈니스 로직 - 모든 채널 불러오기
     *
     */
    public List<Channel> findAllChannel() {
        return channelRepository.findAllChannel();
    }

    /*
     * 비즈니스 로직 - 특정 채널 ID로 찾기
     */
    public Channel findOneChannelById(String channelId) {
        List<Channel> channels = channelRepository.findOneChannelById(channelId);
        if(channels.isEmpty()) throw new NotExistChannelException();
        Channel findChannel = channels.get(0);
        Long timeToLive = channelRepository.findChannelTTL(channelId);
        findChannel.setTimeToLive(timeToLive);
        return findChannel;
    }


    public List<Channel> findChannelByHashName(String tagName) {
        return channelRepository.findOneChannelByHashName(tagName);
    }
}
