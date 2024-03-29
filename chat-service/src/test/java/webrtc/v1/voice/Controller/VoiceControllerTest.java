package webrtc.v1.voice.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;
import webrtc.v1.channel.entity.Channel;
import webrtc.v1.staticgenarator.ChannelGenerator;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.user.service.UsersService;
import webrtc.v1.utils.jwt.service.JwtTokenUtilImpl;
import webrtc.v1.utils.jwt.service.JwtUserDetailsService;
import webrtc.v1.voice.dto.VoiceRoomDto.GetTokenRequest;
import webrtc.v1.voice.dto.VoiceRoomDto.RemoveUserInSessionRequest;
import webrtc.v1.voice.exception.VoiceException.AlreadyRemovedSessionInOpenViduServer;
import webrtc.v1.voice.exception.VoiceException.OpenViduClientException;
import webrtc.v1.voice.service.VoiceRoomService;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@ExtendWith(MockitoExtension.class)
@DataRedisTest
@TestExecutionListeners({})
public class VoiceControllerTest {


  @InjectMocks
  private VoiceController voiceController;
  @Spy
  private JwtTokenUtilImpl jwtTokenUtil;
  @Mock
  private JwtUserDetailsService jwtUserDetailsService;
  @Mock
  private VoiceRoomService voiceRoomService;
  @Mock
  private UsersService usersService;

  private MockMvc mockMvc;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private final String password = "password";
  private final String sessionName1 = "sessionName1";
  private final String token = "token";
  private final String uuid = UUID.randomUUID().toString();
  String jwtAccessToken;

  @BeforeEach
  public void setup(RestDocumentationContextProvider restDocumentationContextProvider)
      throws Exception {
    this.mockMvc = MockMvcBuilders.standaloneSetup(voiceController)
        .apply(documentationConfiguration(restDocumentationContextProvider))
        .addFilters(new CharacterEncodingFilter("UTF-8", true))
        .build();
    doReturn(
        new org.springframework.security.core.userdetails.User(uuid, password, new ArrayList<>()))
        .when(jwtUserDetailsService).loadUserByUsername(any(String.class));

    jwtAccessToken = jwtTokenUtil.generateToken(jwtUserDetailsService.loadUserByUsername(uuid));
  }


  @Test
  public void 토큰발급성공() throws Exception {
    // given

    GetTokenRequest ObjRequest = new GetTokenRequest(sessionName1);
    String StrRequest = objectMapper.writeValueAsString(ObjRequest);

    doReturn(token)
        .when(voiceRoomService).getToken(any(GetTokenRequest.class), any(String.class));

    // when

    // then
    mockMvc.perform(post("/api/v1/webrtc/voice/get-token")
            .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
            .content(StrRequest)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("create-token-success",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                fieldWithPath("channelId").type(STRING).description("세션 이름")
            ),
            responseFields(
                fieldWithPath("token").type(STRING).description("OpenVidu 서버와 통신하기 위해 필요한 token")
            )
        ))
        .andExpect(jsonPath("$.token", is(token)));
  }

  @Test
  public void 토큰발급실패_유저없음() throws Exception {
    // given

    GetTokenRequest ObjRequest = new GetTokenRequest(sessionName1);
    String StrRequest = objectMapper.writeValueAsString(ObjRequest);

    doThrow(NotExistUserException.class)
        .when(voiceRoomService).getToken(any(GetTokenRequest.class), any(String.class));
    // when

    // then

    mockMvc.perform(post("/api/v1/webrtc/voice/get-token")
            .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
            .content(StrRequest)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
        .andExpect(status().is(404))
        .andDo(document("create-token-fail-NotExistUser",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                fieldWithPath("channelId").type(STRING).description("세션 이름 - 채널 id를 넣어주시면 됩니다.")
            )
        ));
  }


  @Test
  public void 토큰발급실패_OpenVidu서버없음() throws Exception {
    // given

    GetTokenRequest ObjRequest = new GetTokenRequest(sessionName1);
    String StrRequest = objectMapper.writeValueAsString(ObjRequest);

    doThrow(new OpenViduClientException())
        .when(voiceRoomService).getToken(any(GetTokenRequest.class), any(String.class));

    // when

    // then

    mockMvc.perform(post("/api/v1/webrtc/voice/get-token")
            .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
            .content(StrRequest)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
        .andExpect(status().is(500))
        .andDo(document("create-token-fail-NotExistOpenviduServer",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                fieldWithPath("channelId").type(STRING).description("세션 이름")
            )
        ));
  }


  @Test
  public void 토큰삭제실패_VoiceRoom없음() throws Exception {
    // given
    Channel channel = ChannelGenerator.createTextChannel();
    RemoveUserInSessionRequest request = new RemoveUserInSessionRequest(
        channel.getId(), token);
    String StrRequest = objectMapper.writeValueAsString(request);

    doThrow(new AlreadyRemovedSessionInOpenViduServer())
        .when(voiceRoomService)
        .removeUserInVoiceRoom(any(RemoveUserInSessionRequest.class), any(String.class));

    // when

    // then

    mockMvc.perform(post("/api/v1/webrtc/voice/remove-user")
            .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
            .content(StrRequest)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
        .andExpect(status().is(404))
        .andDo(document("remove-token-fail-AlreadyRemovedRoom",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                fieldWithPath("channelId").type(STRING).description("세션 이름"),
                fieldWithPath("token").type(STRING).description("음성채팅 토큰")
            )
        ));
  }

  @Test
  public void 유저퇴장성공() throws Exception {
    // given

    RemoveUserInSessionRequest ObjRequest = new RemoveUserInSessionRequest(sessionName1, token);
    String StrRequest = objectMapper.writeValueAsString(ObjRequest);

    doNothing()
        .when(voiceRoomService)
        .removeUserInVoiceRoom(any(RemoveUserInSessionRequest.class), any(String.class));

    // when

    // then

    mockMvc.perform(post("/api/v1/webrtc/voice/remove-user")
            .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
            .content(StrRequest)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("removeuser-success",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                fieldWithPath("channelId").type(STRING).description("세션 이름"),
                fieldWithPath("token").type(STRING).description("발급받았던 토큰")
            )
        ));
  }
}
