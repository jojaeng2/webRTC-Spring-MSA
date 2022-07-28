package webrtc.chatservice.controller.jwt;

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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.JsonFieldType.*;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.CharacterEncodingFilter;
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.User;
import webrtc.chatservice.dto.ChannelDto;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;
import webrtc.chatservice.dto.JwtDto.JwtRequest;
import webrtc.chatservice.dto.UserDto.CreateUserRequest;
import webrtc.chatservice.exception.ChannelException;
import webrtc.chatservice.exception.JwtException;
import webrtc.chatservice.exception.JwtException.JwtAccessTokenNotValid;
import webrtc.chatservice.exception.UserException;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.service.channel.ChannelService;
import webrtc.chatservice.service.jwt.JwtUserDetailsService;
import webrtc.chatservice.service.user.UserService;
import webrtc.chatservice.utils.JwtTokenUtilImpl;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationControllerTest {

    @InjectMocks
    private JwtAuthenticationController jwtAuthenticationController;
    @Spy
    private JwtTokenUtilImpl jwtTokenUtil;
    @Mock
    private JwtUserDetailsService jwtUserDetailsService;
    @Mock
    private UserService userService;
    @Mock
    private ChannelService channelService;
    @Mock
    private HttpApiController httpApiController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    String nickname1 = "nickname1";
    String password = "password";
    String email1 = "email1";
    String jwtAccessToken;


    @BeforeEach
    public void setup(RestDocumentationContextProvider restDocumentationContextProvider) throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(jwtAuthenticationController)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        doReturn(new org.springframework.security.core.userdetails.User(email1, password, new ArrayList<>()))
                .when(jwtUserDetailsService).loadUserByUsername(any(String.class));

        jwtAccessToken = jwtTokenUtil.generateToken(jwtUserDetailsService.loadUserByUsername(email1));
    }

    @Test
    @Transactional
    public void 유저등록성공() throws Exception {
        // given
        CreateUserRequest ObjRequest = new CreateUserRequest(nickname1, password, email1);
        String StrRequest = objectMapper.writeValueAsString(ObjRequest);
        User user = new User(nickname1, password, email1);

        doReturn(new User(nickname1, password, email1))
                .when(userService).saveUser(any(CreateUserRequest.class));

        doReturn(user)
                .when(httpApiController).postSaveUser(any(CreateUserRequest.class));

        // when

        // then
        mockMvc.perform(post("/api/v1/webrtc/chat/register")
                    .content(StrRequest)
                    .contentType(APPLICATION_JSON)
                    .accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andDo(document("register-post",
                    requestFields(
                            fieldWithPath("nickname").type(STRING).description("회원 Nickname"),
                            fieldWithPath("password").type(STRING).description("회원 Password"),
                            fieldWithPath("email").type(STRING).description("회원 Email")
                    ),
                    responseFields(
                            fieldWithPath("id").type(STRING).description("회원 PK"),
                            fieldWithPath("created_at").type(null).description("회원 생성 시간"),
                            fieldWithPath("updated_at").type(null).description("회원 정보 업데이트 시간"),
                            fieldWithPath("email").type(STRING).description("회원 Email"),
                            fieldWithPath("birthdate").type(null).description("회원 생일"),
                            fieldWithPath("phone_number").type(null).description("회원 휴대폰 번호"),
                            fieldWithPath("school").type(null).description("회원 학교 정보"),
                            fieldWithPath("company").type(null).description("회원 회사 정보"),
                            fieldWithPath("nickname").type(null).description("회원 Nickname"),
                            fieldWithPath("nickname_expire_at").type(null).description("회원 닉네임 만료시간")
                    )
            ));
    }

    @Test
    @Transactional
    public void 토큰발급성공() throws Exception {
        // given
        JwtRequest ObjRequest = new JwtRequest(email1,password);
        String StrRequest = objectMapper.writeValueAsString(ObjRequest);
        User user = new User(nickname1, password, email1);

        doReturn(new org.springframework.security.core.userdetails.User(email1, password, new ArrayList<>()))
                .when(jwtUserDetailsService).loadUserByUsername(any(String.class));


        // when

        // then
        mockMvc.perform(post("/api/v1/webrtc/chat/authenticate")
                        .content(StrRequest)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("authenticate-post-success",
                        requestFields(
                                fieldWithPath("email").type(STRING).description("회원 Email"),
                                fieldWithPath("password").type(STRING).description("회원 Password")
                        ),
                        responseFields(
                                fieldWithPath("jwttoken").type(STRING).description("Jwt-Access Token 입니다.")
                        )
                ));
    }

    @Test
    @Transactional
    public void 토큰발급실패_존재하지않는유저() throws Exception {
        // given
        JwtRequest ObjRequest = new JwtRequest(email1,password);
        String StrRequest = objectMapper.writeValueAsString(ObjRequest);
        User user = new User(nickname1, password, email1);

        doThrow(new NotExistUserException())
                .when(jwtUserDetailsService).loadUserByUsername(any(String.class));


        // when

        // then
        mockMvc.perform(post("/api/v1/webrtc/chat/authenticate")
                        .content(StrRequest)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(404))
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("authenticate-post-fail-notexistuser",
                        requestFields(
                                fieldWithPath("email").type(STRING).description("회원 Email"),
                                fieldWithPath("password").type(STRING).description("회원 Password")
                        )
                ));
    }


}
