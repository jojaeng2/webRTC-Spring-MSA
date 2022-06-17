package webrtc.chatservice.service.point;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.Point;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;
import webrtc.chatservice.exception.PointException.InsufficientPointException;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.service.channel.ChannelService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

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

        CreateChannelRequest request = new CreateChannelRequest("testChannel", hashTags);
        User user = new User("testUser", "testUser", "testEmail");
        userRepository.saveUser(user);
        channelService.createChannel(request, "testEmail");
    }

    @Test
    @DisplayName("UserName으로 Point 찾기")
    public void findPointByUserName() {
        // given

        // when
        Point point = pointService.findPointByUserEmail("testEmail");

        // then
        Assertions.assertThat(point.getPoint()).isEqualTo(1000000L);
    }

    @Test
    @DisplayName("User 보유 Point 감소 실패")
    public void decreasePointFail() {
        // given
        Point point = pointService.findPointByUserEmail("testEmail");
        Channel channel = channelService.findChannelByHashName("tag1").get(0);
        // when

        // then
        assertThrows(InsufficientPointException.class,
                () -> pointService.decreasePoint(channel.getId(), "testEmail", 10000000L));
    }

    @Test
    @DisplayName("User 보유 Point 감소 성공")
    public void decreasePointSuccess() {
        // given
        Channel channel = channelService.findChannelByHashName("tag1").get(0);

        // when
        pointService.decreasePoint(channel.getId(), "testEmail", 10000L);
        Point point = pointService.findPointByUserEmail("testEmail");

        // then
        Assertions.assertThat(point.getPoint()).isEqualTo(900000L);
    }
}
