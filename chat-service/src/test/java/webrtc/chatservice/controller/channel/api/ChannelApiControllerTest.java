//package webrtc.chatservice.controller.channel.api;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpHeaders;
//import org.springframework.restdocs.RestDocumentationContextProvider;
//import org.springframework.restdocs.RestDocumentationExtension;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.filter.CharacterEncodingFilter;
//import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;
//import webrtc.chatservice.dto.ChannelDto.FindAllChannelResponse;
//import webrtc.chatservice.dto.JwtDto.JwtRequest;
//import webrtc.chatservice.dto.JwtDto.JwtResponse;
//import webrtc.chatservice.dto.UserDto.CreateUserRequest;
//import webrtc.chatservice.exception.ChannelException;
//import webrtc.chatservice.exception.ChannelException.AlreadyExistChannelException;
//import webrtc.chatservice.service.channel.ChannelService;
//import webrtc.chatservice.service.user.UserService;
//import webrtc.chatservice.utils.CustomJsonMapper;
//import static org.hamcrest.Matchers.is;
//import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
//import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static javax.management.openmbean.SimpleType.LONG;
//import static javax.management.openmbean.SimpleType.STRING;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.InstanceOfAssertFactories.ARRAY;
//import static org.springframework.http.MediaType.APPLICATION_JSON;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
//import static org.springframework.restdocs.payload.PayloadDocumentation.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@AutoConfigureRestDocs
//@SpringBootTest
//@RunWith(SpringRunner.class)
//@AutoConfigureMockMvc
//@ExtendWith(RestDocumentationExtension.class)
//public class ChannelApiControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private WebApplicationContext wac;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private CustomJsonMapper customJsonMapper;
//    @Autowired
//    private ChannelService channelService;
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private RestDocumentationContextProvider restDocumentationContextProvider;
//
//    private String jwtAccessToken;
//    String userName1 = "user1";
//    String userName2 = "user2";
//    String password = "password";
//    String email1 = "email1";
//    String email2 = "email2";
//    String channelName1 = "channelName1";
//    String channelName2 = "channelName2";
//
//    @Before
//    public void setup() throws Exception {
//        this.mockMvc = MockMvcBuilders
//                .webAppContextSetup(wac)
//                .apply(documentationConfiguration(restDocumentationContextProvider))
//                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // ?????? ??????
//                .build();
//    }
//
//    @Before
//    public void registerUser() throws Exception {
//        userService.saveUser(CreateUserRequest(userName1, email1));
//        userService.saveUser(CreateUserRequest(userName2, email2));
//
//
//        ResultActions resultActions2 = mockMvc.perform(post("/api/v1/webrtc/chat/authenticate")
//                .content(new ObjectMapper().writeValueAsString(CreateJwtAccessTokenRequest()))
//                .contentType(APPLICATION_JSON));
//
//        Object object = customJsonMapper.jsonParse(resultActions2.andReturn().getResponse().getContentAsString(), JwtResponse.class);
//        jwtAccessToken = JwtResponse.class.cast(object).getJwttoken();
//    }
//
//    @Test
//    @Transactional
//    public void ?????????_??????_??????_??????() throws Exception{
//        // given
//        CreateChannelRequest request = CreateChannelRequest(channelName1);
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//                post("/api/v1/webrtc/chat/channel")
//                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
//                        .content(objectMapper.writeValueAsString(request))
//                        .contentType(APPLICATION_JSON)
//                        .accept(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(MockMvcResultHandlers.print())
//                .andDo(document("create-channel",
//                        requestHeaders(
//                                headerWithName(HttpHeaders.AUTHORIZATION).description("[jwt (???????????? token)] ??????")
//                        ),
//                        requestFields(
//                                fieldWithPath("channelName").type(STRING).description("?????? ??????"),
//                                fieldWithPath("channelType").type(STRING).description("?????? ??????"),
//                                fieldWithPath("hashTags").type(ARRAY).description("?????? ???????????? ??????")
//                        ),
//                        responseFields(
//                                fieldWithPath("channelName").type(STRING).description("?????? ??????"),
//                                fieldWithPath("limitParticipants").type(LONG).description("?????? ?????? ?????? ??????"),
//                                fieldWithPath("currentParticipants").type(LONG).description("?????? ?????? ?????? ??????"),
//                                fieldWithPath("timeToLive").type(LONG).description("?????? ??????")
//                        )
//                ))
//                .andExpect(jsonPath("$.channelName", is(channelName1)))
//                .andExpect(jsonPath("$.limitParticipants", is(15)))
//                .andExpect(jsonPath("$.currentParticipants", is(1)))
//                .andExpect(jsonPath("$.timeToLive").exists())
//                .andDo(document("create-channel"));
//
//    }
//
//    @Test
//    @Transactional
//    public void ????????????_??????_?????????????????????() throws Exception{
//        // given
//        CreateChannelRequest request = CreateChannelRequest(channelName1);
//        mockMvc.perform(
//                        post("/api/v1/webrtc/chat/channel")
//                                .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
//                                .content(objectMapper.writeValueAsString(request))
//                                .contentType(APPLICATION_JSON)
//                                .accept(APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        // when
//
//
//        // then
//        mockMvc.perform(
//                    post("/api/v1/webrtc/chat/channel")
//                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
//                        .content(objectMapper.writeValueAsString(request))
//                        .contentType(APPLICATION_JSON)
//                        .accept(APPLICATION_JSON))
//                .andExpect(status().is(409));
//    }
//
//    @Test
//    @DisplayName("20??? ?????? Channels ?????? ????????????")
//    public void loadAllChannelsLessThan20() throws Exception{
//        // given
//        int channelsSize = 15;
//        for(int i=0; i<channelsSize; i++) {
//            CreateChannelRequest request = CreateChannelRequest("testChannel" + i);
//            ResultActions resultActions = mockMvc.perform(post("/api/v1/webrtc/channel").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
//                    .content(objectMapper.writeValueAsString(request))
//                    .contentType(APPLICATION_JSON));
//        }
//
//        // when
//        ResultActions resultActions = mockMvc.perform(get("/api/v1/webrtc/channels/0").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken));
//
//        // then
//        resultActions.andExpect(status().isOk());
//        Object obj = customJsonMapper.jsonParse(resultActions.andReturn().getResponse().getContentAsString(), FindAllChannelResponse.class);
//        FindAllChannelResponse response = FindAllChannelResponse.class.cast(obj);
//        assertThat(response.getChannels().size()).isEqualTo(channelsSize);
//    }
//
//    @Test
//    @DisplayName("20??? ?????? Channels ?????? ????????????")
//    public void loadAllChannelsMoreThan20() throws Exception {
//        // given
//        int channelSize = 25;
//        for(int i=0; i<channelSize; i++) {
//            CreateChannelRequest request = CreateChannelRequest("testChannel" + i);
//            ResultActions resultActions = mockMvc.perform(post("/api/v1/webrtc/channel").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
//                    .content(objectMapper.writeValueAsString(request))
//                    .contentType(APPLICATION_JSON));
//        }
//
//        // when
//        ResultActions resultActions0 = mockMvc.perform(get("/api/v1/webrtc/channels/0").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken));
//        ResultActions resultActions1 = mockMvc.perform(get("/api/v1/webrtc/channels/1").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken));
//
//
//        // then
//        resultActions0.andExpect(status().isOk());
//        resultActions1.andExpect(status().isOk());
//
//        Object obj0 = customJsonMapper.jsonParse(resultActions0.andReturn().getResponse().getContentAsString(), FindAllChannelResponse.class);
//        Object obj1 = customJsonMapper.jsonParse(resultActions1.andReturn().getResponse().getContentAsString(), FindAllChannelResponse.class);
//
//        FindAllChannelResponse response0 = FindAllChannelResponse.class.cast(obj0);
//        FindAllChannelResponse response1 = FindAllChannelResponse.class.cast(obj1);
//
//        assertThat(response0.getChannels().size()).isEqualTo(20);
//        assertThat(response1.getChannels().size()).isEqualTo(5);
//    }
//
//    @Test
//    @DisplayName("20??? ?????? My Channels ????????????")
//    public void loadMyChannelsLessThan20() throws Exception{
//        // given
//        int channelsSize = 15;
//        for(int i=0; i<channelsSize; i++) {
//            CreateChannelRequest request = CreateChannelRequest("testChannel" + i);
//            ResultActions resultActions = mockMvc.perform(post("/api/v1/webrtc/channel").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
//                    .content(objectMapper.writeValueAsString(request))
//                    .contentType(APPLICATION_JSON));
//        }
//
//        // when
//        ResultActions resultActions = mockMvc.perform(get("/api/v1/webrtc/mychannel/0").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken));
//
//        // then
//        resultActions.andExpect(status().isOk());
//        Object obj = customJsonMapper.jsonParse(resultActions.andReturn().getResponse().getContentAsString(), FindAllChannelResponse.class);
//        FindAllChannelResponse response = FindAllChannelResponse.class.cast(obj);
//        assertThat(response.getChannels().size()).isEqualTo(channelsSize);
//    }
//
//    @Test
//    @DisplayName("20??? ?????? My Channels ????????????")
//    public void loadMyChannelsMoreThan20() throws Exception {
//
//        // given
//        int channelSize = 25;
//        for(int i=0; i<channelSize; i++) {
//            CreateChannelRequest request = CreateChannelRequest("testChannel" + i);
//            ResultActions resultActions = mockMvc.perform(post("/api/v1/webrtc/channel").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
//                    .content(objectMapper.writeValueAsString(request))
//                    .contentType(APPLICATION_JSON));
//        }
//
//        // when
//        ResultActions resultActions0 = mockMvc.perform(get("/api/v1/webrtc/mychannel/0").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken));
//        ResultActions resultActions1 = mockMvc.perform(get("/api/v1/webrtc/mychannel/1").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken));
//
//
//        // then
//        resultActions0.andExpect(status().isOk());
//        resultActions1.andExpect(status().isOk());
//
//        Object obj0 = customJsonMapper.jsonParse(resultActions0.andReturn().getResponse().getContentAsString(), FindAllChannelResponse.class);
//        Object obj1 = customJsonMapper.jsonParse(resultActions1.andReturn().getResponse().getContentAsString(), FindAllChannelResponse.class);
//
//        FindAllChannelResponse response0 = FindAllChannelResponse.class.cast(obj0);
//        FindAllChannelResponse response1 = FindAllChannelResponse.class.cast(obj1);
//
//        assertThat(response0.getChannels().size()).isEqualTo(20);
//        assertThat(response1.getChannels().size()).isEqualTo(5);
//    }
//
//    @Test
//    @DisplayName("????????? ????????? ?????? ?????? ??? ?????? ?????? ??????")
//    public void enterNewUserReturnChannelInfo() throws Exception {
//        // given
//        CreateChannelRequest request = CreateChannelRequest("testChannel");
//        mockMvc.perform(post("/api/v1/webrtc/channel").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
//                .content(objectMapper.writeValueAsString(request))
//                .contentType(APPLICATION_JSON));
//
//        // when
////        /*Channel findChannel = channelService.findChannelByHashName("testTag1").get(0);
////        channelService.enterChannel(findChannel, "enterEmail");
////        ResultActions resultActions = mockMvc.perform(get("/api/v1/webrtc/channels/0").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken));
////        Object obj = customJsonMapper.jsonParse(resultActions.andReturn().getResponse().getContentAsString(), FindAllChannelResponse.class);
////        FindAllChannelResponse allResponse = FindAllChannelResponse.class.cast(obj);
////        ChannelResponse response = allResponse.getChannels().get(0);
////
////        // then
////        resultActions.andExpect(status().isOk());
////        assertThat(response.getCurrentParticipant*/s()).isEqualTo(2);
//    }
//
//    @Test
//    @DisplayName("channelId??? ?????? ?????? ?????? ??????")
//    public void findOneChannelByChannelId() throws Exception{
//        // given
//        CreateChannelRequest request = CreateChannelRequest("testChannel");
//        mockMvc.perform(post("/api/v1/webrtc/channel").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
//                .content(objectMapper.writeValueAsString(request))
//                .contentType(APPLICATION_JSON));
//
//
//        // when
////        Channel findChannel = channelService.findChannelByHashName("testTag1").get(0);
////        ResultActions resultActions = mockMvc.perform(get("/api/v1/webrtc/channel/" + findChannel.getId()).header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken));
////        Object obj = customJsonMapper.jsonParse(resultActions.andReturn().getResponse().getContentAsString(), FindOneChannelResponse.class);
////        FindOneChannelResponse response = FindOneChannelResponse.class.cast(obj);
////
////        // then
////        resultActions.andExpect(status().isOk());
////        assertThat(response.getChannelId()).isEqualTo(findChannel.getId());
////        assertThat(response.getChannelName()).isEqualTo(findChannel.getChannelName());
////        assertThat(response.getCurrentParticipants()).isEqualTo(1L);
//    }
//
//
//
//    private JwtRequest CreateJwtAccessTokenRequest() {
//        return new JwtRequest(email1, password);
//    }
//
//    private CreateUserRequest CreateUserRequest(String userName, String email) {
//        return new CreateUserRequest(userName, password, email);
//    }
//
//
//    private CreateChannelRequest CreateChannelRequest(String channelName) {
//        List<String> hashTags = new ArrayList<>();
//        hashTags.add("testTag1");
//        hashTags.add("testTag2");
//        hashTags.add("testTag3");
//        return new CreateChannelRequest(channelName, hashTags, "chat");
//    }
//}
