package webrtc.v1.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.point.entity.Point;
import webrtc.v1.point.repository.PointRepository;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.user.repository.UsersRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService{

    private final PointRepository pointRepository;
    private final UsersRepository userRepository;

    @Transactional(readOnly = true)
    public int findPointSum(String userId) {
        Users user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(NotExistUserException::new);
        return pointRepository.findByUser(user).stream()
                .map(Point::getAmount)
                .reduce(0, Integer::sum);
    }
}
