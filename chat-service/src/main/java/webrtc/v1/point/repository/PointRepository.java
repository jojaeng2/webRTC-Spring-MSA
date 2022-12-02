package webrtc.v1.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webrtc.v1.point.entity.Point;
import webrtc.v1.user.entity.Users;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long> {

    List<Point> findByUser(Users user);
}
