package webrtc.openvidu.repository.point;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.openvidu.domain.Point;
import webrtc.openvidu.domain.User;
import webrtc.openvidu.dto.ChannelDto;
import webrtc.openvidu.dto.ChannelDto.CreateChannelRequest;
import webrtc.openvidu.repository.user.UserRepository;
import webrtc.openvidu.service.channel.ChannelService;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
public class PointRepositoryImplTest {

    @Autowired
    private ChannelService channelService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PointRepository pointRepository;

    @BeforeEach
    public void saveChannelAndUser() {
        List<String> hashTags = new ArrayList<>();
        hashTags.add("tag1");
        hashTags.add("tag2");
        hashTags.add("tag2");

        CreateChannelRequest request = new CreateChannelRequest("testChannel", hashTags);
        User user = new User("testUser", "testUser");
        userRepository.saveUser(user);
        channelService.createChannel(request, "testUser");
    }

    @Test
    @DisplayName("userName으로 Point 객체 찾기")
    public void findPointByUserName() {
        // given

        // when
        Point findPoint = pointRepository.findPointByUserName("testUser");

        // then
        Assertions.assertThat(findPoint.getPoint()).isEqualTo(1000000);
    }

    @Test
    @DisplayName("Point 객체 point 감소 성공")
    public void decreasePointSuccess(){
        // given

        // when

        // then
    }

}
