package webrtc.openvidu.repository.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import webrtc.openvidu.domain.Point;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.repository.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository{

    @PersistenceContext
    private EntityManager em;

    private final UserRepository userRepository;

    public Point findPointByUserName(String userName) {
        User user = userRepository.findUsersByName(userName).get(0);
        return user.getPoint();
    }
}
