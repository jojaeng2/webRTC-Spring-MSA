package webrtc.chatservice.repository.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import webrtc.chatservice.domain.Point;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.repository.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository{

    @PersistenceContext
    private EntityManager em;

    private final UserRepository userRepository;

    public Point findPointByUserEmail(String userEmail) {
        User user = userRepository.findUserByEmail(userEmail);
        return user.getPoint();
    }

    public void decreasePoint(Point point, Long requiredPoint) {

        point.setPoint(point.getPoint() - requiredPoint);
    }
}
