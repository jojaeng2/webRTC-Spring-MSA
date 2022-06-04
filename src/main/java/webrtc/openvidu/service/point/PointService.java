package webrtc.openvidu.service.point;

public interface PointService {

    void decreasePoint();

    Long findPointByUserName(String userName);
}
