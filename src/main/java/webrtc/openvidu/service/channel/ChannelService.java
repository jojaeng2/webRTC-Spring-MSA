package webrtc.openvidu.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.domain.channel.Channel;
import webrtc.openvidu.dto.channel.CreateChannelRequest;
import webrtc.openvidu.repository.channel.ChannelRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private Long channelNum = 1L;
    private final Long limitParticipants = 15L;

    /**
     * 비즈니스 로직 - 채널 생성
     * @Param channelName
     * @Param limitParticipants
     *
     * @return Channel
     */
    public Channel createChannel(CreateChannelRequest request) {
        String channelName = request.getChannelName();
        Long limitParticipants = request.getLimitParticipants();
        Channel channel = new Channel(channelName, limitParticipants, channelNum++);
        channelRepository.createChannel(channel);
        return channel;
    }

    /*
     * 비즈니스 로직 - 채널 입장
     *
     */
    public int enterChannel(String channelId, Long userId) {
        Channel channel = channelRepository.findOneChannelById(channelId);
        Long limitParticipants = channel.getLimitParticipants();
        Long currentParticipants = channel.getCurrentParticipants();
        if(limitParticipants.equals(currentParticipants)) {
            return 0;
        }
        else {
            // User 정보 찾아와야함.
            User user = new User();
            channelRepository.enterChannel(channel, user);
            channelRepository.updateChannel(channel);
            return 1;
        }
    }

    /*
     * 비즈니스 로직 - 채널 퇴장
     *
     */
    public void leaveChannel(String channelId, Long userId) {
        Channel channel = channelRepository.findOneChannelById(channelId);
        // userRepository 만들면 변경할것
        User user = new User();
        channelRepository.leaveChannel(channel, user);
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
    public void updateChannel(Channel channel) {
        channelRepository.updateChannel(channel);
    }

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
        return findChannel;
    }

}
