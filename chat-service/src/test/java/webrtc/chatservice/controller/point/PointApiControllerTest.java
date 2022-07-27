package webrtc.chatservice.controller.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.ChannelDto;
import webrtc.chatservice.dto.ChannelDto.ExtensionChannelTTLRequest;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.service.channel.ChannelService;
import webrtc.chatservice.service.user.UserService;
import webrtc.chatservice.utils.JwtTokenUtil;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ChannelType.VOIP;

@Import({
        ObjectMapper.class
})
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class PointApiControllerTest {
//
//    @InjectMocks
//    private PointApiController pointApiController;
//
//    @Mock
//    private ChannelService channelService;
//    @Mock
//    private JwtTokenUtil jwtTokenUtil;
//    @Mock
//    private UserService userService;
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private MockMvc mockMvc;
//
//    String nickname1 = "nickname1";
//    String nickname2 = "nickname2";
//    String password = "password";
//    String email1 = "email1";
//    String email2 = "email2";
//    String channelName1 = "channelName1";
//    String channelName2 = "channelName2";
//    String tag1 = "tag1";
//    String tag2 = "tag2";
//    String tag3 = "tag3";
//    String jwtAccessToken = "accessToken";
//    ChannelType text = TEXT;
//    ChannelType voip = VOIP;
//
//    @BeforeEach
//    public void init() {
//        mockMvc = MockMvcBuilders.standaloneSetup(pointApiController).build();
//    }
//
//    @Test
//    @Transactional
//    public void 채널연장성공() throws Exception{
//        // given
//        Channel channel = new Channel(channelName1, text);
//        User user = new User(nickname1, password, email1);
//        Long requestTTL = 100L;
//
//        ExtensionChannelTTLRequest objectRequest = new ExtensionChannelTTLRequest(requestTTL);
//        String request = objectMapper.writeValueAsString(objectRequest);
//
//        doNothing()
//                .when(channelService).extensionChannelTTL(channel.getId(), user.getEmail(), requestTTL);
//
//        // when
//
//        // then
//        mockMvc.perform(
//                post("/api/v1/webrtc/chat/extension/{id}")
//                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
//
//        )
//    }

}
