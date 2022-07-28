package webrtc.chatservice.controller.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.HashTag;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;
import webrtc.chatservice.dto.ChannelDto.FindAllChannelResponse;
import webrtc.chatservice.dto.JwtDto.JwtRequest;
import webrtc.chatservice.dto.JwtDto.JwtResponse;
import webrtc.chatservice.dto.UserDto.CreateUserRequest;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.ChannelException;
import webrtc.chatservice.exception.ChannelException.AlreadyExistChannelException;
import webrtc.chatservice.exception.JwtException;
import webrtc.chatservice.service.channel.ChannelService;
import webrtc.chatservice.service.jwt.JwtUserDetailsService;
import webrtc.chatservice.service.user.UserService;
import webrtc.chatservice.utils.CustomJsonMapper;
import webrtc.chatservice.utils.JwtTokenUtilImpl;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static webrtc.chatservice.enums.ChannelType.TEXT;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@ExtendWith(MockitoExtension.class)
public class ChannelApiControllerTest {

    @InjectMocks
    private ChannelApiController channelApiController;
    @Spy
    private JwtTokenUtilImpl jwtTokenUtil;
    @Mock
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private CustomJsonMapper customJsonMapper;
    @Mock
    private ChannelService channelService;
    @Mock
    private HttpApiController httpApiController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    String nickname1 = "user1";
    String password = "password";
    String email1 = "email1";
    String email2 = "email2";
    String channelName1 = "channelName1";
    String channelName2 = "channelName2";
    String tag1 = "tag1";
    String tag2 = "tag2";
    String tag3 = "tag3";
    ChannelType text = TEXT;
    List<String> hashTagList = new ArrayList<String>();
    String jwtAccessToken;


    @BeforeEach
    public void setup(RestDocumentationContextProvider restDocumentationContextProvider) throws Exception {
        hashTagList.add(tag1);
        hashTagList.add(tag2);
        hashTagList.add(tag3);

        this.mockMvc = MockMvcBuilders.standaloneSetup(channelApiController)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        // jwt token 생성
        doReturn(new org.springframework.security.core.userdetails.User(email1, password, new ArrayList<>()))
                .when(jwtUserDetailsService).loadUserByUsername(any(String.class));

        jwtAccessToken = jwtTokenUtil.generateToken(jwtUserDetailsService.loadUserByUsername(email1));
    }



    @Test
    @Transactional
    public void 새로운_채널생성_성공() throws Exception{
        // given

        User user = new User(nickname1, password, email1);

        CreateChannelRequest ObjRequest = new CreateChannelRequest(channelName1, hashTagList, text);
        String StrRequest = objectMapper.writeValueAsString(ObjRequest);

        doReturn(user.getEmail())
                .when(jwtTokenUtil).getUserEmailFromToken(any());

        doReturn(new Channel(channelName1, text))
                .when(channelService).createChannel(any(CreateChannelRequest.class), any(String.class));

        // when

        // then
        mockMvc.perform(post("/api/v1/webrtc/chat/channel")
                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                        .content(StrRequest)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("create-channel-success",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Jwt Access 토큰")
                        ),
                        requestFields(
                                fieldWithPath("channelName").type(STRING).description("채널 이름"),
                                fieldWithPath("hashTags").type(JsonFieldType.ARRAY).description("채널 해시태그 목록"),
                                fieldWithPath("channelType").type(STRING).description("채널 타입")
                        ),
                        responseFields(
                                fieldWithPath("channelName").type(STRING).description("채널 이름"),
                                fieldWithPath("limitParticipants").type(NUMBER).description("채널에 참여할 수있는 제한인원"),
                                fieldWithPath("currentParticipants").type(NUMBER).description("채널에 현재 참여중인 인원"),
                                fieldWithPath("timeToLive").type(NUMBER).description("채널이 삭제되기까지 남은 시간")
                        )
                ))
                .andExpect(jsonPath("$.channelName", is(channelName1)))
                .andExpect(jsonPath("$.limitParticipants", is(15)))
                .andExpect(jsonPath("$.timeToLive").exists());

    }

    @Test
    @Transactional
    public void 채널생성_실패_중복된채널이름() throws Exception{
        // given
        User user = new User(nickname1, password, email1);

        CreateChannelRequest ObjRequest = new CreateChannelRequest(channelName1, hashTagList, text);
        String StrRequest = objectMapper.writeValueAsString(ObjRequest);

        doReturn(user.getEmail())
                .when(jwtTokenUtil).getUserEmailFromToken(any());

        doThrow(new AlreadyExistChannelException())
                .when(channelService).createChannel(any(CreateChannelRequest.class), any(String.class));

        // when


        // then
        mockMvc.perform(post("/api/v1/webrtc/chat/channel")
                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                        .content(StrRequest)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(409))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("create-channel-fail-alreadyExistName",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Jwt Access 토큰")
                        ),
                        requestFields(
                                fieldWithPath("channelName").type(STRING).description("채널 이름"),
                                fieldWithPath("hashTags").type(JsonFieldType.ARRAY).description("채널 해시태그 목록"),
                                fieldWithPath("channelType").type(STRING).description("채널 타입")
                        )
                ));
    }
    @Test
    @Transactional
    public void jwt토큰문제발생() throws Exception{
        // given
        User user = new User(nickname1, password, email1);

        CreateChannelRequest ObjRequest = new CreateChannelRequest(channelName1, hashTagList, text);
        String StrRequest = objectMapper.writeValueAsString(ObjRequest);

        doReturn(user.getEmail())
                .when(jwtTokenUtil).getUserEmailFromToken(any());

        doThrow(new JwtException.JwtAccessTokenNotValid())
                .when(channelService).createChannel(any(CreateChannelRequest.class), any(String.class));

        // when


        // then
        mockMvc.perform(post("/api/v1/webrtc/chat/channel")
                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                        .content(StrRequest)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(401))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("jwt-accesstoken-invalid",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Jwt Access 토큰")
                        ),
                        requestFields(
                                fieldWithPath("channelName").type(STRING).description("채널 이름"),
                                fieldWithPath("hashTags").type(JsonFieldType.ARRAY).description("채널 해시태그 목록"),
                                fieldWithPath("channelType").type(STRING).description("채널 타입")
                        )
                ));
    }

//
//    @Test
//    @DisplayName("20개 이하 Channels 정보 불러오기")
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
//    @DisplayName("20개 이상 Channels 정보 불러오기")
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
//    @DisplayName("20개 이하 My Channels 불러오기")
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
//    @DisplayName("20개 이상 My Channels 불러오기")
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
//    @DisplayName("새로운 유저가 채널 입장 후 채널 정보 반환")
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
//    @DisplayName("channelId로 특정 채널 정보 반환")
//    public void findOneChannelByChannelId() throws Exception{
//        // given
//        CreateChannelRequest request = CreateChannelRequest("testChannel");
//        mockMvc.perform(post("/api/v1/webrtc/channel").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
//                .content(objectMapper.writeValueAsString(request))
//                .contentType(APPLICATION_JSON));


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
//    }



}
