package webrtc.openvidu.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webrtc.openvidu.domain.ChannelHashTag;
import webrtc.openvidu.domain.HashTag;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.dto.channel.CreateChannelRequest;
import webrtc.openvidu.enums.ChannelServiceReturnType;
import webrtc.openvidu.repository.ChannelHashTagRepository;
import webrtc.openvidu.repository.ChannelRepository;

import java.util.List;

import static webrtc.openvidu.enums.ChannelServiceReturnType.FULLCHANNEL;
import static webrtc.openvidu.enums.ChannelServiceReturnType.SUCCESS;

@RequiredArgsConstructor
@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final ChannelHashTagRepository channelHashTagRepository;
    private final Long limitParticipants = 15L;

    /**
     * 비즈니스 로직 - 채널 생성
     * @Param channelName
     * @Param limitParticipants
     *
     * @return Channel
     */
    public Channel createChannel(CreateChannelRequest request) {
        return channelRepository.createChannel(request);
    }

    /*
     * 비즈니스 로직 - 채널 입장
     *
     */
    public ChannelServiceReturnType enterChannel(String channelId, Long userId) {
        Channel channel = channelRepository.findOneChannelById(channelId);
        Long limitParticipants = channel.getLimitParticipants();
        Long currentParticipants = channel.getCurrentParticipants();
        if(limitParticipants.equals(currentParticipants)) {
            return FULLCHANNEL;
        }
        else {
            // User 정보 찾아와야함.
            User user = new User();
            channelRepository.enterChannel(channel, user);
            channelRepository.updateChannel(channel);
            return SUCCESS;
        }
    }

    /*
     * 비즈니스 로직 - 채널 퇴장
     *
     */
    public Channel leaveChannel(String channelId, Long userId) {
        Channel channel = channelRepository.findOneChannelById(channelId);
        // userRepository 만들면 변경할것
        User user = new User();
        channelRepository.leaveChannel(channel, user);
        return channelRepository.updateChannel(channel);
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
        Channel findChannel = channelRepository.findOneChannelById(channelId);
        Long timeToLive = channelRepository.findChannelTTL(channelId);
        findChannel.setTimeToLive(timeToLive);
        return findChannel;
    }

}
