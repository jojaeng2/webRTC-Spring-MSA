package webrtc.chatservice.controller.channel.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;
import webrtc.chatservice.dto.ChannelDto.FindAllChannelResponse;
import webrtc.chatservice.dto.JwtDto.JwtRequest;
import webrtc.chatservice.dto.JwtDto.JwtResponse;
import webrtc.chatservice.dto.UserDto.CreateUserRequest;
import webrtc.chatservice.service.channel.ChannelService;
import webrtc.chatservice.service.user.UserService;
import webrtc.chatservice.utils.CustomJsonMapper;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.List;

import static javax.management.openmbean.SimpleType.LONG;
import static javax.management.openmbean.SimpleType.STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.ARRAY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@Transactional
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
public class ChannelApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomJsonMapper customJsonMapper;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestDocumentationContextProvider restDocumentationContextProvider;

    private String jwtAccessToken;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .build();

        ResultActions resultActions0 = mockMvc.perform(post("/api/v1/webrtc/chat/register")
                .content(new ObjectMapper().writeValueAsString(CreateUserRequest("user1", "email1")))
                .contentType(APPLICATION_JSON));

        ResultActions resultActions1 = mockMvc.perform(post("/api/v1/webrtc/chat/register")
                .content(new ObjectMapper().writeValueAsString(CreateUserRequest("user2", "email2")))
                .contentType(APPLICATION_JSON));


        ResultActions resultActions2 = mockMvc.perform(post("/api/v1/webrtc/chat/authenticate")
                .content(new ObjectMapper().writeValueAsString(CreateJwtAccessTokenRequest()))
                .contentType(APPLICATION_JSON));

        Object object = customJsonMapper.jsonParse(resultActions2.andReturn().getResponse().getContentAsString(), JwtResponse.class);
        jwtAccessToken = JwtResponse.class.cast(object).getJwttoken();
    }

    @Test
    public void 새로운_채널_생성_성공() throws Exception{
        // given
        String channelName = "testChannel";
        CreateChannelRequest request = CreateChannelRequest(channelName);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/webrtc/chat/channel")
                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("create-channel",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("[jwt (발급받은 token)] 형식")
                        ),
                        requestFields(
                                fieldWithPath("channelName").type(STRING).description("채널 이름"),
                                fieldWithPath("channelType").type(STRING).description("채널 타입"),
                                fieldWithPath("hashTags").type(ARRAY).description("채널 해시태그 목록")
                        ),
                        responseFields(
                                fieldWithPath("channelName").type(STRING).description("채널 이름"),
                                fieldWithPath("limitParticipants").type(LONG).description("채널 참가 제한 인원"),
                                fieldWithPath("currentParticipants").type(LONG).description("채널 참가 현재 인원"),
                                fieldWithPath("timeToLive").type(LONG).description("채널 수명")
                        )
                ))
                .andExpect(jsonPath("$.channelName", is(channelName)))
                .andExpect(jsonPath("$.limitParticipants", is(15)))
                .andExpect(jsonPath("$.currentParticipants", is(1)))
                .andExpect(jsonPath("$.timeToLive").exists())
                .andDo(document("create-channel"));

    }

    @Test
    @DisplayName("중복된 채널 생성")
    public void createDuplicateChannel() throws Exception{
        // given
        CreateChannelRequest request = CreateChannelRequest("testChannel");

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
            CreateChannelRequest request = CreateChannelRequest("testChannel" + i);
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
            CreateChannelRequest request = CreateChannelRequest("testChannel" + i);
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
            CreateChannelRequest request = CreateChannelRequest("testChannel" + i);
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
            CreateChannelRequest request = CreateChannelRequest("testChannel" + i);
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
        CreateChannelRequest request = CreateChannelRequest("testChannel");
        mockMvc.perform(post("/api/v1/webrtc/channel").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON));

        // when
//        /*Channel findChannel = channelService.findChannelByHashName("testTag1").get(0);
//        channelService.enterChannel(findChannel, "enterEmail");
//        ResultActions resultActions = mockMvc.perform(get("/api/v1/webrtc/channels/0").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken));
//        Object obj = customJsonMapper.jsonParse(resultActions.andReturn().getResponse().getContentAsString(), FindAllChannelResponse.class);
//        FindAllChannelResponse allResponse = FindAllChannelResponse.class.cast(obj);
//        ChannelResponse response = allResponse.getChannels().get(0);
//
//        // then
//        resultActions.andExpect(status().isOk());
//        assertThat(response.getCurrentParticipant*/s()).isEqualTo(2);
    }

    @Test
    @DisplayName("channelId로 특정 채널 정보 반환")
    public void findOneChannelByChannelId() throws Exception{
        // given
        CreateChannelRequest request = CreateChannelRequest("testChannel");
        mockMvc.perform(post("/api/v1/webrtc/channel").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON));


        // when
//        Channel findChannel = channelService.findChannelByHashName("testTag1").get(0);
//        ResultActions resultActions = mockMvc.perform(get("/api/v1/webrtc/channel/" + findChannel.getId()).header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken));
//        Object obj = customJsonMapper.jsonParse(resultActions.andReturn().getResponse().getContentAsString(), FindOneChannelResponse.class);
//        FindOneChannelResponse response = FindOneChannelResponse.class.cast(obj);
//
//        // then
//        resultActions.andExpect(status().isOk());
//        assertThat(response.getChannelId()).isEqualTo(findChannel.getId());
//        assertThat(response.getChannelName()).isEqualTo(findChannel.getChannelName());
//        assertThat(response.getCurrentParticipants()).isEqualTo(1L);
    }



    private JwtRequest CreateJwtAccessTokenRequest() {
        return new JwtRequest("email1", "password");
    }

    private CreateUserRequest CreateUserRequest(String userName, String email) {
        return new CreateUserRequest(userName, "password", email);
    }


    private CreateChannelRequest CreateChannelRequest(String channelName) {
        List<String> hashTags = new ArrayList<>();
        hashTags.add("testTag1");
        hashTags.add("testTag2");
        hashTags.add("testTag3");
        return new CreateChannelRequest(channelName, hashTags, "chat");
    }
}
