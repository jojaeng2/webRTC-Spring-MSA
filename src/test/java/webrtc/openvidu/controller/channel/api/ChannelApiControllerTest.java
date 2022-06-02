package webrtc.openvidu.controller.channel.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import webrtc.openvidu.dto.ChannelDto.CreateChannelRequest;
import webrtc.openvidu.dto.JwtDto.JwtRequest;
import webrtc.openvidu.dto.JwtDto.JwtResponse;
import webrtc.openvidu.dto.UserDto.CreateUserRequest;
import webrtc.openvidu.repository.user.UserRepository;
import webrtc.openvidu.utils.CustomJsonMapper;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ChannelApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext ctx;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomJsonMapper customJsonMapper;

    private String jwtAccessToken;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .build();

        ResultActions ra = mockMvc.perform(post("/api/v1/webrtc/register")
                .content(new ObjectMapper().writeValueAsString(CreateUserRequest()))
                .contentType(APPLICATION_JSON));
        ResultActions resultActions = mockMvc.perform(post("/api/v1/webrtc/authenticate")
                .content(new ObjectMapper().writeValueAsString(CreateJwtAccessTokenRequest()))
                .contentType(APPLICATION_JSON));

        Object object = customJsonMapper.jsonParse(resultActions.andReturn().getResponse().getContentAsString(), JwtResponse.class);
        jwtAccessToken = JwtResponse.class.cast(object).getJwttoken();
    }

    @Test
    @DisplayName("새로운 채널 생성 성공")
    public void createChannelSuccess() throws Exception{
        // given
        CreateChannelRequest request = createChannelRequest("testChannel");

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/webrtc/channel").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("중복된 채널 생성")
    public void createDuplicateChannel() throws Exception{
        // given
        CreateChannelRequest request = createChannelRequest("testChannel");

        ResultActions resultActions1 = mockMvc.perform(post("/api/v1/webrtc/channel").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON));

        // when
        ResultActions resultActions2 = mockMvc.perform(post("/api/v1/webrtc/channel").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON));

        // then
        resultActions1.andExpect(status().isOk());
        resultActions2.andExpect(status().isConflict());
    }

    private JwtRequest CreateJwtAccessTokenRequest() {
        return new JwtRequest("user", "user");
    }

    private CreateUserRequest CreateUserRequest() {
        return new CreateUserRequest("user", "user");
    }


    private CreateChannelRequest createChannelRequest(String channelName) {
        List<String> hashTags = new ArrayList<>();
        hashTags.add("testTag1");
        hashTags.add("testTag2");
        hashTags.add("testTag3");
        return new CreateChannelRequest(channelName, hashTags);
    }
}
