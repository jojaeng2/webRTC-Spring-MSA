package webrtc.chatservice.service.point;

import webrtc.chatservice.domain.Point;

public interface PointService {

    void decreasePoint(String channelId, String userName, Long requestTTL);

    Point findPointByUserEmail(String userEmail);
}
