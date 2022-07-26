package webrtc.chatservice.service.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.UserDto.DecreasePointRequest;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.repository.channel.ChannelHashTagRepository;
import webrtc.chatservice.repository.channel.ChannelDBRepository;
import webrtc.chatservice.repository.channel.ChannelUserRepository;
import webrtc.chatservice.repository.chat.ChatLogRepository;
import webrtc.chatservice.repository.user.UserRepository;
import webrtc.chatservice.utils.CustomJsonMapper;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static webrtc.chatservice.enums.ChannelType.TEXT;


@RunWith(SpringRunner.class)
@ExtendWith(MockitoExtension.class)
@RestClientTest({
        ChannelServiceImpl.class
})
public class PointDecreaseMockTest {

//    String nickname1 = "nickname1";
//    String nickname2 = "nickname2";
//    String password = "password";
//    String email1 = "email1";
//    String email2 = "email2";
//    String channelName1 = "channelName1";
//    String tag1 = "tag1";
//    String tag2 = "tag2";
//    String tag3 = "tag3";
//    ChannelType text = TEXT;
//
//    @MockBean
//    private PlatformTransactionManager manager;
//    @MockBean
//    private ChannelDBRepository channelDBRepository;
//    @MockBean
//    private UserRepository userRepository;
//    @MockBean
//    private ChannelHashTagRepository channelHashTagRepository;
//    @MockBean
//    private ChatLogRepository chatLogRepository;
//    @MockBean
//    private ChannelUserRepository channelUserRepository;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @MockBean
//    private CustomJsonMapper customJsonMapper;
//
//
//
//    @Autowired
//    private ChannelService channelService;
//    RestTemplate restTemplate = new RestTemplate();
//    @Autowired
//    private MockRestServiceServer server;
//
//
//
//    @Before
//    public void 테스트용_유저생성() {
//        User user1 = new User(nickname1, password, email1);
//        userRepository.saveUser(user1);
//
//        User user2 = new User(nickname2, password, email2);
//        userRepository.saveUser(user2);
//    }
//
//    @Before
//    public void 테스트용_채널생성() {
//        Channel channel = new Channel(channelName1, text);
//        channelDBRepository.createChannel(channel, returnHashTags());
//    }
//
//    @Before
//    public void setUP() {
//        server = MockRestServiceServer.createServer(restTemplate);
//    }
//
//
//    @Test
//    @Transactional
//    public void 채널수명연장성공() throws Exception {
//        // given
//        Channel channel = channelDBRepository.findChannelByChannelName(channelName1);
//        User user = userRepository.findUserByEmail(email1);
//        int point = 100;
//        Long requestTTL = 10L;
//
//        DecreasePointRequest request = new DecreasePointRequest(user.getEmail(), point);
//
//        server.expect(
//                    requestTo("http://auth-service:8080/api/v1/webrtc/auth/decrease/point"))
//                .andExpect(method(POST))
//                .andExpect(content().json(objectMapper.writeValueAsString(request)))
//                .andRespond(
//                        withStatus(HttpStatus.OK)
//                );
//
//        // when
//        channelService.extensionChannelTTL(channel.getId(), user.getEmail(), requestTTL);
//
//        // then
//
//    }
//
//    public List<String> returnHashTags() {
//        List<String> hashTags = new ArrayList<>();
//        hashTags.add(tag1);
//        hashTags.add(tag2);
//        hashTags.add(tag3);
//        return hashTags;
//    }
}
