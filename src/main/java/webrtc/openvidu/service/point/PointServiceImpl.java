package webrtc.openvidu.service.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.domain.Point;
import webrtc.openvidu.exception.PointException;
import webrtc.openvidu.exception.PointException.InsufficientPointException;
import webrtc.openvidu.repository.channel.ChannelRepository;
import webrtc.openvidu.repository.point.PointRepository;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService{

    private final PointRepository pointRepository;
    private final ChannelRepository channelRepository;

    @Transactional
    public void decreasePoint(String channelId, String userEmail, Long requestTTL) {
        Point userPoint = findPointByUserEmail(userEmail);
        if(userPoint.getPoint() < requestTTL * 10L) {
            throw new InsufficientPointException();
        }
        Channel channel = channelRepository.findChannelsById(channelId).get(0);
        channelRepository.extensionChannelTTL(channel, requestTTL/10L);
        pointRepository.decreasePoint(userPoint.getId(), requestTTL*10L);
    }

    public Point findPointByUserEmail(String userEmail) {
        return pointRepository.findPointByUserEmail(userEmail);
    }
}
