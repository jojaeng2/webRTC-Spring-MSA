package webrtc.openvidu.service.point;

import webrtc.openvidu.domain.Point;

public interface PointService {

    void decreasePoint(String channelId, String userName, Long requestTTL);

    Point findPointByUserEmail(String userEmail);
}
