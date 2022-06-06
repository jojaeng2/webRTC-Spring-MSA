package webrtc.openvidu.repository.point;

import webrtc.openvidu.domain.Point;

public interface PointRepository {

    Point findPointByUserEmail(String userEmail);

    void decreasePoint(Long pointId, Long requiredPoint);
}
