package webrtc.openvidu.service.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webrtc.openvidu.domain.Point;
import webrtc.openvidu.exception.PointException;
import webrtc.openvidu.exception.PointException.InsufficientPointException;
import webrtc.openvidu.repository.point.PointRepository;

@Service
@RequiredArgsConstructor
public class PointServiceImpl {

    private final PointRepository pointRepository;

    public void decreasePoint(String userName, Long requiredPoint) {
        Point userPoint = findPointByUserName(userName);
        if(userPoint.getPoint() < requiredPoint) {
            throw new InsufficientPointException();
        }
        pointRepository.decreasePoint(userPoint.getId(), requiredPoint);
    }

    public Point findPointByUserName(String userName) {
        return pointRepository.findPointByUserName(userName);
    }
}
