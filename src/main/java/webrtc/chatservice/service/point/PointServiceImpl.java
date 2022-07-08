package webrtc.chatservice.service.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.Point;
import webrtc.chatservice.exception.PointException.InsufficientPointException;
import webrtc.chatservice.repository.channel.ChannelRepository;
import webrtc.chatservice.repository.point.PointRepository;

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
        pointRepository.decreasePoint(userPoint, requestTTL*10L);
    }

    public Point findPointByUserEmail(String userEmail) {
        return pointRepository.findPointByUserEmail(userEmail);
    }
}
