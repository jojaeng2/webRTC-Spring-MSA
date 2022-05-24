package webrtc.openvidu.repository.point;

import webrtc.openvidu.domain.Point;

public interface PointRepository {

    Point findPointByUserName(String userName);


}
