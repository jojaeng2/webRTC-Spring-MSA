package webrtc.openvidu.service.point;

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
import webrtc.openvidu.repository.user.UserRepository;
import webrtc.openvidu.service.channel.ChannelService;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
public class PointServiceImplTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelService channelService;


    @BeforeEach
    public void saveChannelAndUser() {
        List<String> hashTags = new ArrayList<>();
        hashTags.add("tag1");
        hashTags.add("tag2");
        hashTags.add("tag2");

        ChannelDto.CreateChannelRequest request = new ChannelDto.CreateChannelRequest("testChannel", hashTags);
        User user = new User("testUser", "testUser");
        userRepository.saveUser(user);
        channelService.createChannel(request, "testUser");
    }

    @Test
    @DisplayName("UserName으로 Point 찾기")
    public void findPointByUserName() {
        // given

        // when
        Point point = pointService.findPointByUserName("testUser");

        // then
        Assertions.assertThat(point.getPoint()).isEqualTo(1000000L);

    }
}
