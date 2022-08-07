package webrtc.chatservice.controller.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.CharacterEncodingFilter;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.ChannelDto.ExtensionChannelInfoWithUserPointResponse;
import webrtc.chatservice.dto.ChannelDto.ExtensionChannelTTLRequest;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.ChannelException.NotExistChannelException;
import webrtc.chatservice.exception.PointException.InsufficientPointException;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.service.channel.ChannelService;
import webrtc.chatservice.service.jwt.JwtUserDetailsService;
import webrtc.chatservice.service.users.UsersService;
import webrtc.chatservice.utils.JwtTokenUtilImpl;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static webrtc.chatservice.enums.ChannelType.TEXT;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@ExtendWith(MockitoExtension.class)
public class PointApiControllerTest {

    @InjectMocks
    private PointApiController pointApiController;
    @Spy
    private JwtTokenUtilImpl jwtTokenUtil;
    @Mock
    private JwtUserDetailsService jwtUserDetailsService;
    @Mock
    private ChannelService channelService;
    @Mock
    private UsersService usersService;
    @Mock
    private HttpApiController httpApiController;

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
    public void setup(RestDocumentationContextProvider restDocumentationContextProvider) throws Exception{

        this.mockMvc = MockMvcBuilders.standaloneSetup(pointApiController)
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
    public void 채널수명_유저정보_요청성공() throws Exception{
        // given
        Channel channel = new Channel(channelName1, text);
        int point = 100000;
        Long channelTTL = 1234567L;

        doReturn(new ExtensionChannelInfoWithUserPointResponse(channelTTL, point))
                .when(usersService).findUserWithPointByEmail(any(String.class), any(String.class));

        // when

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/webrtc/chat/point/{id}", channel.getId())
                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("findTTLAndPoint-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("채널ID입니다.")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Jwt Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("channelTTL").type(NUMBER).description("채널의 현재 남은 TTL 시간입니다."),
                                fieldWithPath("point").type(NUMBER).description("유저가 현재 가지고 있는 포인트입니다.")
                                )
                ));
    }



    @Test
    @Transactional
    public void 채널수명_유저정보_요청실패_채널없음() throws Exception{
        // given
        Channel channel = new Channel(channelName1, text);

        doThrow(new NotExistChannelException())
                .when(usersService).findUserWithPointByEmail(any(String.class), any(String.class));

        // when

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/webrtc/chat/point/{id}", channel.getId())
                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(404))
                .andDo(document("findTTLAndPoint-fail-notExistChannel",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("채널ID입니다.")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Jwt Access 토큰")
                        )
                ));
    }


    @Test
    @Transactional
    public void 채널수명_유저정보_요청실패_유저없음() throws Exception{
        // given
        Channel channel = new Channel(channelName1, text);

        doThrow(new NotExistUserException())
                .when(usersService).findUserWithPointByEmail(any(String.class), any(String.class));

        // when

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/webrtc/chat/point/{id}", channel.getId())
                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(404))
                .andDo(document("findTTLAndPoint-fail-notExistUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("채널ID입니다.")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Jwt Access 토큰")
                        )
                ));
    }



    @Test
    @Transactional
    public void 채널연장성공() throws Exception{

        // given
        Channel channel = new Channel(channelName1, text);
        Users users = new Users(nickname1, password, email1);
        Long requestTTL = 10L;

        ExtensionChannelTTLRequest ObjRequest = new ExtensionChannelTTLRequest(requestTTL);
        String StrRequest = objectMapper.writeValueAsString(ObjRequest);

        doNothing()
                .when(channelService).extensionChannelTTL(any(String.class), any(String.class), any(Long.class));
        doReturn(channel)
                .when(channelService).findOneChannelById(channel.getId());

        // when

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/webrtc/chat/extension/{id}", channel.getId())
                .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                .content(StrRequest)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("extensionTTL-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("채널ID입니다.")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Jwt Access 토큰")
                        ),
                        requestFields(
                                fieldWithPath("requestTTL").type(NUMBER).description("TTL을 증가시키는 단위입니다.")
                        ),
                        responseFields(
                                fieldWithPath("channelTTL").type(NUMBER).description("TTL 증가 후 남은 시간입니다.")
                        )
                ));
    }

    @Test
    @Transactional
    public void 채널연장실패_채널없음() throws Exception{

        // given
        Channel channel = new Channel(channelName1, text);
        Users users = new Users(nickname1, password, email1);
        Long requestTTL = 10L;

        ExtensionChannelTTLRequest ObjRequest = new ExtensionChannelTTLRequest(requestTTL);
        String StrRequest = objectMapper.writeValueAsString(ObjRequest);

        doThrow(new NotExistChannelException())
                .when(channelService).extensionChannelTTL(any(String.class), any(String.class), any(Long.class));

        // when

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/webrtc/chat/extension/{id}", channel.getId())
                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                        .content(StrRequest)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(404))
                .andDo(document("extensionTTL-fail-notexistchannel",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("채널ID입니다.")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Jwt Access 토큰")
                        ),
                        requestFields(
                                fieldWithPath("requestTTL").type(NUMBER).description("TTL을 증가시키는 단위입니다.")
                        )
                ));
    }


    @Test
    @Transactional
    public void 채널연장실패_포인트부족() throws Exception{

        // given
        Channel channel = new Channel(channelName1, text);
        Users users = new Users(nickname1, password, email1);
        Long requestTTL = 10L;

        ExtensionChannelTTLRequest ObjRequest = new ExtensionChannelTTLRequest(requestTTL);
        String StrRequest = objectMapper.writeValueAsString(ObjRequest);

        doThrow(new InsufficientPointException())
                .when(channelService).extensionChannelTTL(any(String.class), any(String.class), any(Long.class));

        // when

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/webrtc/chat/extension/{id}", channel.getId())
                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                        .content(StrRequest)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(409))
                .andDo(document("extensionTTL-fail-point",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("채널ID입니다.")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Jwt Access 토큰")
                        ),
                        requestFields(
                                fieldWithPath("requestTTL").type(NUMBER).description("TTL을 증가시키는 단위입니다.")
                        )
                ));
    }
}
