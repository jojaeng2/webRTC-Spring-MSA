package webrtc.openvidu.service.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.domain.Point;
import webrtc.openvidu.exception.PointException;
import webrtc.openvidu.exception.PointException.InsufficientPointException;
import webrtc.openvidu.repository.point.PointRepository;
import webrtc.openvidu.service.channel.ChannelService;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService{

    private final PointRepository pointRepository;
    private final ChannelService channelService;

    public void decreasePoint(String channelId, String userName, Long requestTTL) {
        Point userPoint = findPointByUserName(userName);
        if(userPoint.getPoint() < requestTTL * 10L) {
            throw new InsufficientPointException();
        }
        Channel channel = channelService.findOneChannelById(channelId);
        channelService.extensionChannelTTL(channel, requestTTL/10L);
        pointRepository.decreasePoint(userPoint.getId(), requestTTL*10L);
    }

    public Point findPointByUserName(String userName) {
        return pointRepository.findPointByUserName(userName);
    }
}
