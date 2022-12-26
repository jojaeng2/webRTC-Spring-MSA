package webrtc.v1.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webrtc.v1.point.entity.Point;
import webrtc.v1.point.repository.PointRepository;
import webrtc.v1.user.entity.Users;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.user.repository.UsersRepository;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PointRepository pointRepository;
    private final UsersRepository userRepository;

    @Transactional(readOnly = true)
    public int findPointSum(String userId) {
        Users user = findUsersById(userId);
        return getPointSumByUser(user);
    }

    private Users findUsersById(String id) {
        return userRepository.findById(id)
                .orElseThrow(NotExistUserException::new);
    }

    private int getPointSumByUser(Users user) {
        return pointRepository.findByUser(user).stream()
                .map(Point::getAmount)
                .reduce(0, Integer::sum);
    }
}
