package webrtc.openvidu.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webrtc.openvidu.domain.ChannelUser;
import webrtc.openvidu.domain.HashTag;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.dto.ChannelDto.CreateChannelRequest;
import webrtc.openvidu.exception.ChannelException.AlreadyExistChannelException;
import webrtc.openvidu.exception.ChannelException.AlreadyExistUserEnterChannelException;
import webrtc.openvidu.exception.ChannelException.ChannelParticipantsFullException;
import webrtc.openvidu.exception.ChannelException.NotExistChannelException;
import webrtc.openvidu.repository.ChannelRepository;
import webrtc.openvidu.service.user.UserService;

import java.util.List;


@RequiredArgsConstructor
@Service
public class ChannelService {


    private final ChannelRepository channelRepository;
    private final UserService userService;
    private final ChannelUserService channelUserService;
    private final Long limitParticipants = 15L;

    /**
     * 비즈니스 로직 - 채널 생성
     * @Param channelName
     * @Param limitParticipants
     *
     * @return Channel
     */
    public Channel createChannel(CreateChannelRequest request, String userName) {
        List<Channel> channels = channelRepository.findOneChannelByChannelName(request.getChannelName());
        if(!channels.isEmpty()) {
            throw new AlreadyExistChannelException();
        }
        Channel channel = new Channel(request.getChannelName());
        User user = userService.findUserByName(userName);
        List<String> hashTags = request.getHashTags();
        channelRepository.createChannel(channel, hashTags);

        ChannelUser channelUser = new ChannelUser(channel, user);
        channel.addChannelUser(channelUser);
        user.addChannelUser(channelUser);
        channelUserService.save(channelUser);
        return channel;
    }

    /*
     * 비즈니스 로직 - 채널 입장
     *
     */
    public void enterChannel(String channelId, String userName) {
        User user = userService.findUserByName(userName);

        List<Channel> findEnterChannels = channelRepository.findChannelsByUserId(channelId, user.getId());
        Channel requestEnterChannel = findOneChannelById(channelId);
        if(!findEnterChannels.isEmpty()) {
            throw new AlreadyExistUserEnterChannelException();
        }
        else {
            Long limitParticipants = requestEnterChannel.getLimitParticipants();
            Long currentParticipants = requestEnterChannel.getCurrentParticipants();
            if(limitParticipants.equals(currentParticipants)) {
                throw new ChannelParticipantsFullException();
            }
            else {
                ChannelUser channelUser = new ChannelUser(requestEnterChannel, user);
                requestEnterChannel.addChannelUser(channelUser);
                user.addChannelUser(channelUser);
                channelUserService.save(channelUser);
            }
        }
    }

    /*
     * 비즈니스 로직 - 채널 퇴장
     *
     */
    public void exitChannel(String channelId, String userName) {
        User user = userService.findUserByName(userName);
        channelUserService.exitChannel(channelId, user.getId());
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
        List<Channel> channels = channelRepository.findAllChannel();
        for (Channel channel : channels) {
            channel.setTimeToLive(channelRepository.findChannelTTL(channel.getId()));
        }
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
        return channelRepository.findChannelsByHashName(tagName);
    }
}
