package webrtc.v1.controller.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.CharacterEncodingFilter;
import webrtc.v1.domain.Channel;
import webrtc.v1.domain.ChannelHashTag;
import webrtc.v1.domain.HashTag;
import webrtc.v1.domain.Users;
import webrtc.v1.dto.ChannelDto.CreateChannelRequest;
import webrtc.v1.enums.ChannelType;
import webrtc.v1.exception.ChannelException.AlreadyExistChannelException;
import webrtc.v1.exception.ChannelException.NotExistChannelException;
import webrtc.v1.exception.JwtException;
import webrtc.v1.service.channel.ChannelFindService;
import webrtc.v1.service.channel.ChannelLifeService;
import webrtc.v1.service.jwt.JwtUserDetailsService;
import webrtc.v1.utils.jwt.JwtTokenUtilImpl;
import webrtc.v1.utils.log.trace.ThreadLocalLogTrace;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static webrtc.v1.enums.ChannelType.TEXT;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@ExtendWith(MockitoExtension.class)
public class ChannelApiControllerTest {

    @InjectMocks
    private ChannelApiController channelApiController;
    @Spy
    private JwtTokenUtilImpl jwtTokenUtil;
    @Spy
    private ThreadLocalLogTrace logTrace;
    @Mock
    private JwtUserDetailsService jwtUserDetailsService;
    @Mock
    private ChannelLifeService channelLifeService;

    @Mock
    private ChannelFindService channelFindService;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    String nickname1 = "user1";
    String password = "password";
    String email1 = "email1";
    String channelName1 = "channelName1";
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

        Users users2 = Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();

        CreateChannelRequest ObjRequest = new CreateChannelRequest(channelName1, hashTagList, text);
        String StrRequest = objectMapper.writeValueAsString(ObjRequest);


        doReturn(Channel.builder()
                .channelName(channelName1)
                .channelType(text)
                .build())
                .when(channelLifeService).create(any(CreateChannelRequest.class), any(String.class));

        // when

        // then
        mockMvc.perform(post("/api/v1/webrtc/chat/channel")
                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                        .content(StrRequest)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("create-channel-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Jwt Access 토큰")
                        ),
                        requestFields(
                                fieldWithPath("channelName").type(STRING).description("채널 이름"),
                                fieldWithPath("hashTags").type(ARRAY).description("채널 해시태그 목록"),
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
        Users users2 = Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();

        CreateChannelRequest ObjRequest = new CreateChannelRequest(channelName1, hashTagList, text);
        String StrRequest = objectMapper.writeValueAsString(ObjRequest);

        doReturn(users2.getEmail())
                .when(jwtTokenUtil).getUserEmailFromToken(any());

        doThrow(new AlreadyExistChannelException())
                .when(channelLifeService).create(any(CreateChannelRequest.class), any(String.class));

        // when


        // then
        mockMvc.perform(post("/api/v1/webrtc/chat/channel")
                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                        .content(StrRequest)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(409))
                .andDo(document("create-channel-fail-alreadyExistName",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Jwt Access 토큰")
                        ),
                        requestFields(
                                fieldWithPath("channelName").type(STRING).description("채널 이름"),
                                fieldWithPath("hashTags").type(ARRAY).description("채널 해시태그 목록"),
                                fieldWithPath("channelType").type(STRING).description("채널 타입")
                        )
                ));
    }
    @Test
    @Transactional
    public void jwt토큰문제발생() throws Exception{
        // given
        Users users2 = Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();

        CreateChannelRequest ObjRequest = new CreateChannelRequest(channelName1, hashTagList, text);
        String StrRequest = objectMapper.writeValueAsString(ObjRequest);

        doReturn(users2.getEmail())
                .when(jwtTokenUtil).getUserEmailFromToken(any());

        doThrow(new JwtException.JwtAccessTokenNotValid())
                .when(channelLifeService).create(any(CreateChannelRequest.class), any(String.class));

        // when


        // then
        mockMvc.perform(post("/api/v1/webrtc/chat/channel")
                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                        .content(StrRequest)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(401))
                .andDo(document("jwt-accesstoken-invalid",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Jwt Access 토큰")
                        ),
                        requestFields(
                                fieldWithPath("channelName").type(STRING).description("채널 이름"),
                                fieldWithPath("hashTags").type(ARRAY).description("채널 해시태그 목록"),
                                fieldWithPath("channelType").type(STRING).description("채널 타입")
                        )
                ));
    }


    @Test
    @Transactional
    public void 채널ID로_채널정보반환성공() throws Exception{

        // given

        Channel channel = Channel.builder()
                .channelName(channelName1)
                .channelType(text)
                .build();
        for(String tagName : hashTagList) {
            HashTag hashTag = HashTag
                    .builder()
                    .tagName(tagName)
                    .build();
            ChannelHashTag channelHashTag = ChannelHashTag.builder()
                    .channel(channel)
                    .hashTag(hashTag)
                    .build();
            channel.addChannelHashTag(channelHashTag);
            hashTag.addChannelHashTag(channelHashTag);
        }

        doReturn(channel)
                .when(channelFindService).findById(any(String.class));


        // when

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/webrtc/chat/channel/{id}", channel.getId())
                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("return-channelInfo-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        pathParameters(
                                parameterWithName("id").description("채널ID입니다.")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Jwt Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("id").type(STRING).description("채널 이름"),
                                fieldWithPath("channelName").type(STRING).description("채널 이름"),
                                fieldWithPath("limitParticipants").type(NUMBER).description("채널에 참여할 수있는 제한인원"),
                                fieldWithPath("currentParticipants").type(NUMBER).description("채널에 현재 참여중인 인원"),
                                fieldWithPath("timeToLive").type(NUMBER).description("채널이 삭제되기까지 남은 시간"),
                                fieldWithPath("channelHashTags").type(ARRAY).description("채널에 사용된 해시태그들"),
                                fieldWithPath("channelHashTags[].hashTag").type(OBJECT).description("채널에 사용된 해시태그"),
                                fieldWithPath("channelHashTags[].hashTag.tagName").type(STRING).description("해시태그 이름"),
                                fieldWithPath("channelType").type(STRING).description("채널 타입")
                        )
                ))
                .andExpect(jsonPath("$.channelName", is(channelName1)))
                .andExpect(jsonPath("$.limitParticipants", is(15)))
                .andExpect(jsonPath("$.timeToLive").exists())
                .andExpect(jsonPath("$.channelHashTags").exists());

    }


    @Test
    @Transactional
    public void 채널ID로_채널정보반환실패_채널없음() throws Exception{

        // given

        Channel channel = Channel.builder()
                .channelName(channelName1)
                .channelType(text)
                .build();

        doThrow(new NotExistChannelException())
                .when(channelFindService).findById(any(String.class));


        // when

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/webrtc/chat/channel/{id}", channel.getId())
                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(404))
                .andDo(document("return-channelInfo-fail-notexistchannel",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        pathParameters(
                                parameterWithName("id").description("채널ID입니다.")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Jwt Access 토큰")
                        )
                ));
    }


    @Test
    @Transactional
    public void any채널목록불러오기성공() throws Exception{
        // given
        List<Channel> channels = new ArrayList<>();
        int channelsSize = 2;
        for(int i=1; i<=channelsSize; i++) {
            Channel channel = Channel.builder()
                    .channelName(channelName1)
                    .channelType(text)
                    .build();
            for(String tagName : hashTagList) {
                HashTag hashTag = HashTag.builder()
                        .tagName(tagName)
                        .build();
                ChannelHashTag channelHashTag = ChannelHashTag.builder()
                        .channel(channel)
                        .hashTag(hashTag)
                        .build();
                channel.addChannelHashTag(channelHashTag);
                hashTag.addChannelHashTag(channelHashTag);
            }
            channels.add(channel);
        }

        doReturn(channels)
                .when(channelFindService).findAnyChannel(any(String.class), any(Integer.class));

        // when

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/webrtc/chat/channels/{orderType}/{idx}", "partiDESC", "0")
                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(200))
                .andDo(document("return-anychannels-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("orderType").description("채널 목록을 불러올 정렬 기준입니다."),
                                parameterWithName("idx").description("몇번째 채널 목록을 불러올지 알려주는 index 값입니다.")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Jwt Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("channels").type(ARRAY).description("채널 정보를 담고있는 배열"),
                                fieldWithPath("channels[].id").type(STRING).description("채널 이름"),
                                fieldWithPath("channels[].channelName").type(STRING).description("채널 이름"),
                                fieldWithPath("channels[].limitParticipants").type(NUMBER).description("채널에 참여할 수있는 제한인원"),
                                fieldWithPath("channels[].currentParticipants").type(NUMBER).description("채널에 현재 참여중인 인원"),
                                fieldWithPath("channels[].timeToLive").type(NUMBER).description("채널이 삭제되기까지 남은 시간"),
                                fieldWithPath("channels[].channelHashTags").type(ARRAY).description("채널에 사용된 해시태그들"),
                                fieldWithPath("channels[].channelHashTags[].hashTag").type(OBJECT).description("채널에 사용된 해시태그"),
                                fieldWithPath("channels[].channelHashTags[].hashTag.tagName").type(STRING).description("해시태그 이름"),
                                fieldWithPath("channels[].channelType").type(STRING).description("채널 타입")
                        )
                ));
    }

    @Test
    @Transactional
    public void my채널목록불러오기성공() throws Exception{
        // given
        List<Channel> channels = new ArrayList<>();
        int channelsSize = 2;
        for(int i=1; i<=channelsSize; i++) {
            Channel channel = Channel.builder()
                    .channelName(channelName1)
                    .channelType(text)
                    .build();
            for(String tagName : hashTagList) {
                HashTag hashTag = HashTag.builder()
                        .tagName(tagName)
                        .build();
                ChannelHashTag channelHashTag = ChannelHashTag.builder()
                        .channel(channel)
                        .hashTag(hashTag)
                        .build();

                hashTag.addChannelHashTag(channelHashTag);
                channel.addChannelHashTag(channelHashTag);
            }
            channels.add(channel);
        }

        doReturn(channels)
                .when(channelFindService).findMyChannel(any(String.class), any(String.class), any(Integer.class));

        // when

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/webrtc/chat/mychannel/{orderType}/{idx}", "partiDESC", "0")
                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(200))
                .andDo(document("return-mychannels-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("orderType").description("채널 목록을 불러올 정렬 기준입니다."),
                                parameterWithName("idx").description("몇번째 채널 목록을 불러올지 알려주는 index 값입니다.")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Jwt Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("channels").type(ARRAY).description("채널 정보를 담고있는 배열"),
                                fieldWithPath("channels[].id").type(STRING).description("채널 이름"),
                                fieldWithPath("channels[].channelName").type(STRING).description("채널 이름"),
                                fieldWithPath("channels[].limitParticipants").type(NUMBER).description("채널에 참여할 수있는 제한인원"),
                                fieldWithPath("channels[].currentParticipants").type(NUMBER).description("채널에 현재 참여중인 인원"),
                                fieldWithPath("channels[].timeToLive").type(NUMBER).description("채널이 삭제되기까지 남은 시간"),
                                fieldWithPath("channels[].channelHashTags").type(ARRAY).description("채널에 사용된 해시태그들"),
                                fieldWithPath("channels[].channelHashTags[].hashTag").type(OBJECT).description("채널에 사용된 해시태그"),
                                fieldWithPath("channels[].channelHashTags[].hashTag.tagName").type(STRING).description("해시태그 이름"),
                                fieldWithPath("channels[].channelType").type(STRING).description("채널 타입")
                        )
                ));
    }
}
