package webrtc.chatservice.repository.point;

import webrtc.chatservice.domain.Point;

public interface PointRepository {

    Point findPointByUserEmail(String userEmail);

    void decreasePoint(Point point, Long requiredPoint);
}
