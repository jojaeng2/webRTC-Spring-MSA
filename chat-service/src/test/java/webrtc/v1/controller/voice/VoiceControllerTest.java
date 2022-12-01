package webrtc.v1.controller.voice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;
import webrtc.v1.domain.Users;
import webrtc.v1.dto.voice.SessionDto.GetTokenRequest;
import webrtc.v1.dto.voice.SessionDto.RemoveUserInSessionRequest;
import webrtc.v1.exception.UserException.NotExistUserException;
import webrtc.v1.exception.VoiceException.OpenViduClientException;
import webrtc.v1.service.user.UsersService;
import webrtc.v1.service.voice.VoiceRoomService;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@ExtendWith(MockitoExtension.class)
@DataRedisTest
@TestExecutionListeners({})
public class VoiceControllerTest {


    @InjectMocks
    private VoiceController voiceController;

    @Mock
    private VoiceRoomService voiceRoomService;
    @Mock
    private UsersService usersService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String nickname1 = "user1";
    private final String password = "password";
    private final String email1 = "email1";
    private final String sessionName1 = "sessionName1";
    private final String token = "token";

    @BeforeEach
    public void setup(RestDocumentationContextProvider restDocumentationContextProvider) throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(voiceController)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }


    @Test
    public void 토큰발급성공() throws Exception {
        // given

        GetTokenRequest ObjRequest = new GetTokenRequest(sessionName1, email1);
        String StrRequest = objectMapper.writeValueAsString(ObjRequest);

        doReturn(createUsers())
                .when(usersService).findOneByEmail(any(String.class));

        doReturn(token)
                .when(voiceRoomService).getToken(any(GetTokenRequest.class), any(Users.class));

        // when

        // then
        mockMvc.perform(post("/api/v1/webrtc/voice/get-token")
                        .content(StrRequest)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("create-token-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("sessionName").type(STRING).description("세션 이름"),
                                fieldWithPath("email").type(STRING).description("유저 Email")
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

        GetTokenRequest ObjRequest = new GetTokenRequest(sessionName1, email1);
        String StrRequest = objectMapper.writeValueAsString(ObjRequest);

        doThrow(new NotExistUserException())
                .when(usersService).findOneByEmail(any(String.class));

        // when



        // then

        mockMvc.perform(post("/api/v1/webrtc/voice/get-token")
                        .content(StrRequest)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(404))
                .andDo(document("create-token-fail-NotExistUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("sessionName").type(STRING).description("세션 이름 - 채널 id를 넣어주시면 됩니다."),
                                fieldWithPath("email").type(STRING).description("유저 Email")
                        )
                ));
    }


    @Test
    public void 토큰발급실패_OpenVidu서버없음() throws Exception {
        // given

        GetTokenRequest ObjRequest = new GetTokenRequest(sessionName1, email1);
        String StrRequest = objectMapper.writeValueAsString(ObjRequest);

        doReturn(createUsers())
                .when(usersService).findOneByEmail(any(String.class));

        doThrow(new OpenViduClientException())
                .when(voiceRoomService).getToken(any(GetTokenRequest.class), any(Users.class));

        // when



        // then

        mockMvc.perform(post("/api/v1/webrtc/voice/get-token")
                        .content(StrRequest)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(500))
                .andDo(document("create-token-fail-NotExistOpenviduServer",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("sessionName").type(STRING).description("세션 이름"),
                                fieldWithPath("email").type(STRING).description("유저 Email")
                        )
                ));
    }



    @Test
    public void 유저퇴장성공() throws Exception {
        // given

        RemoveUserInSessionRequest ObjRequest = new RemoveUserInSessionRequest(sessionName1, email1, token);
        String StrRequest = objectMapper.writeValueAsString(ObjRequest);

        doReturn(createUsers())
                .when(usersService).findOneByEmail(any(String.class));

        doNothing()
                .when(voiceRoomService).removeUserInVoiceRoom(any(RemoveUserInSessionRequest.class), any(Users.class));

        // when



        // then

        mockMvc.perform(post("/api/v1/webrtc/voice/remove-user")
                        .content(StrRequest)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("removeuser-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("sessionName").type(STRING).description("세션 이름"),
                                fieldWithPath("email").type(STRING).description("유저 Email"),
                                fieldWithPath("token").type(STRING).description("발급받았던 토큰")
                        )
                ));
    }

    @Test
    public void 유저퇴장실패_유저없음() throws Exception {
        // given

        RemoveUserInSessionRequest ObjRequest = new RemoveUserInSessionRequest(sessionName1, email1, token);
        String StrRequest = objectMapper.writeValueAsString(ObjRequest);

        doThrow(new NotExistUserException())
                .when(usersService).findOneByEmail(any(String.class));

        // when



        // then

        mockMvc.perform(post("/api/v1/webrtc/voice/remove-user")
                        .content(StrRequest)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(404))
                .andDo(document("removeuser-fail-NotExistUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("sessionName").type(STRING).description("세션 이름"),
                                fieldWithPath("email").type(STRING).description("유저 Email"),
                                fieldWithPath("token").type(STRING).description("발급받았던 토큰")
                        )
                ));
    }

    Users createUsers() {
        return Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();
    }
}