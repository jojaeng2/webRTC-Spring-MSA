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
import webrtc.openvidu.domain.Channel;
import webrtc.openvidu.dto.ChannelDto.ChannelResponse;
import webrtc.openvidu.dto.ChannelDto.CreateChannelRequest;
import webrtc.openvidu.dto.ChannelDto.FindAllChannelResponse;
import webrtc.openvidu.dto.JwtDto.JwtRequest;
import webrtc.openvidu.dto.JwtDto.JwtResponse;
import webrtc.openvidu.dto.UserDto.CreateUserRequest;
import webrtc.openvidu.service.channel.ChannelService;
import webrtc.openvidu.utils.CustomJsonMapper;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private CustomJsonMapper customJsonMapper;

    @Autowired
    private ChannelService channelService;

    private String jwtAccessToken;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .build();

        ResultActions resultActions0 = mockMvc.perform(post("/api/v1/webrtc/register")
                .content(new ObjectMapper().writeValueAsString(CreateUserRequest("user")))
                .contentType(APPLICATION_JSON));

        ResultActions resultActions1 = mockMvc.perform(post("/api/v1/webrtc/register")
                .content(new ObjectMapper().writeValueAsString(CreateUserRequest("enterUser")))
                .contentType(APPLICATION_JSON));


        ResultActions resultActions2 = mockMvc.perform(post("/api/v1/webrtc/authenticate")
                .content(new ObjectMapper().writeValueAsString(CreateJwtAccessTokenRequest()))
                .contentType(APPLICATION_JSON));

        Object object = customJsonMapper.jsonParse(resultActions2.andReturn().getResponse().getContentAsString(), JwtResponse.class);
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

    @Test
    @DisplayName("20개 이하 Channels 정보 불러오기")
    public void loadAllChannelsLessThan20() throws Exception{
        // given
        int channelsSize = 15;
        for(int i=0; i<channelsSize; i++) {
            CreateChannelRequest request = createChannelRequest("testChannel" + i);
            ResultActions resultActions = mockMvc.perform(post("/api/v1/webrtc/channel").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON));
        }

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/webrtc/channels/0").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken));

        // then
        resultActions.andExpect(status().isOk());
        Object obj = customJsonMapper.jsonParse(resultActions.andReturn().getResponse().getContentAsString(), FindAllChannelResponse.class);
        FindAllChannelResponse response = FindAllChannelResponse.class.cast(obj);
        assertThat(response.getChannels().size()).isEqualTo(channelsSize);
    }

    @Test
    @DisplayName("20개 이상 Channels 정보 불러오기")
    public void loadAllChannelsMoreThan20() throws Exception {
        // given
        int channelSize = 25;
        for(int i=0; i<channelSize; i++) {
            CreateChannelRequest request = createChannelRequest("testChannel" + i);
            ResultActions resultActions = mockMvc.perform(post("/api/v1/webrtc/channel").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON));
        }

        // when
        ResultActions resultActions0 = mockMvc.perform(get("/api/v1/webrtc/channels/0").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken));
        ResultActions resultActions1 = mockMvc.perform(get("/api/v1/webrtc/channels/1").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken));


        // then
        resultActions0.andExpect(status().isOk());
        resultActions1.andExpect(status().isOk());

        Object obj0 = customJsonMapper.jsonParse(resultActions0.andReturn().getResponse().getContentAsString(), FindAllChannelResponse.class);
        Object obj1 = customJsonMapper.jsonParse(resultActions1.andReturn().getResponse().getContentAsString(), FindAllChannelResponse.class);

        FindAllChannelResponse response0 = FindAllChannelResponse.class.cast(obj0);
        FindAllChannelResponse response1 = FindAllChannelResponse.class.cast(obj1);

        assertThat(response0.getChannels().size()).isEqualTo(20);
        assertThat(response1.getChannels().size()).isEqualTo(5);
    }

    @Test
    @DisplayName("20개 이하 My Channels 불러오기")
    public void loadMyChannelsLessThan20() throws Exception{
        // given
        int channelsSize = 15;
        for(int i=0; i<channelsSize; i++) {
            CreateChannelRequest request = createChannelRequest("testChannel" + i);
            ResultActions resultActions = mockMvc.perform(post("/api/v1/webrtc/channel").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON));
        }

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/webrtc/mychannel/0").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken));

        // then
        resultActions.andExpect(status().isOk());
        Object obj = customJsonMapper.jsonParse(resultActions.andReturn().getResponse().getContentAsString(), FindAllChannelResponse.class);
        FindAllChannelResponse response = FindAllChannelResponse.class.cast(obj);
        assertThat(response.getChannels().size()).isEqualTo(channelsSize);
    }

    @Test
    @DisplayName("20개 이상 My Channels 불러오기")
    public void loadMyChannelsMoreThan20() throws Exception {

        // given
        int channelSize = 25;
        for(int i=0; i<channelSize; i++) {
            CreateChannelRequest request = createChannelRequest("testChannel" + i);
            ResultActions resultActions = mockMvc.perform(post("/api/v1/webrtc/channel").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON));
        }

        // when
        ResultActions resultActions0 = mockMvc.perform(get("/api/v1/webrtc/mychannel/0").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken));
        ResultActions resultActions1 = mockMvc.perform(get("/api/v1/webrtc/mychannel/1").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken));


        // then
        resultActions0.andExpect(status().isOk());
        resultActions1.andExpect(status().isOk());

        Object obj0 = customJsonMapper.jsonParse(resultActions0.andReturn().getResponse().getContentAsString(), FindAllChannelResponse.class);
        Object obj1 = customJsonMapper.jsonParse(resultActions1.andReturn().getResponse().getContentAsString(), FindAllChannelResponse.class);

        FindAllChannelResponse response0 = FindAllChannelResponse.class.cast(obj0);
        FindAllChannelResponse response1 = FindAllChannelResponse.class.cast(obj1);

        assertThat(response0.getChannels().size()).isEqualTo(20);
        assertThat(response1.getChannels().size()).isEqualTo(5);
    }

    @Test
    @DisplayName("새로운 유저가 채널 입장 후 채널 정보 반환")
    public void enterNewUserReturnChannelInfo() throws Exception {
        // given
        CreateChannelRequest request = createChannelRequest("testChannel");
        mockMvc.perform(post("/api/v1/webrtc/channel").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON));

        // when
        Channel findChannel = channelService.findChannelByHashName("testTag1").get(0);
        channelService.enterChannel(findChannel, "enterUser");
        ResultActions resultActions = mockMvc.perform(get("/api/v1/webrtc/channels/0").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken));
        Object obj = customJsonMapper.jsonParse(resultActions.andReturn().getResponse().getContentAsString(), FindAllChannelResponse.class);
        FindAllChannelResponse allResponse = FindAllChannelResponse.class.cast(obj);
        ChannelResponse response = allResponse.getChannels().get(0);

        // then
        resultActions.andExpect(status().isOk());
        assertThat(response.getCurrentParticipants()).isEqualTo(2);
    }



    private JwtRequest CreateJwtAccessTokenRequest() {
        return new JwtRequest("user", "user");
    }

    private CreateUserRequest CreateUserRequest(String userName) {
        return new CreateUserRequest(userName, "user");
    }


    private CreateChannelRequest createChannelRequest(String channelName) {
        List<String> hashTags = new ArrayList<>();
        hashTags.add("testTag1");
        hashTags.add("testTag2");
        hashTags.add("testTag3");
        return new CreateChannelRequest(channelName, hashTags);
    }
}
