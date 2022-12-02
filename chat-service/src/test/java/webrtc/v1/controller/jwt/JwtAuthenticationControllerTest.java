package webrtc.v1.controller.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.*;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.CharacterEncodingFilter;
import webrtc.v1.config.CustomPasswordEncoder;
import webrtc.v1.user.controller.UserController;
import webrtc.v1.user.entity.Users;
import webrtc.v1.utils.jwt.dto.JwtDto.JwtRequest;
import webrtc.v1.user.dto.UsersDto.CreateUserRequest;
import webrtc.v1.user.exception.UserException.NotExistUserException;
import webrtc.v1.utils.jwt.JwtUserDetailsService;
import webrtc.v1.user.service.UsersService;
import webrtc.v1.utils.jwt.JwtTokenUtilImpl;

import java.util.ArrayList;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureRestDocs
@Import(CustomPasswordEncoder.class)
@ExtendWith(RestDocumentationExtension.class)
@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationControllerTest {

    @InjectMocks
    private UserController userController;
    @Spy
    private JwtTokenUtilImpl jwtTokenUtil;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUserDetailsService jwtUserDetailsService;
    @Mock
    private UsersService usersService2;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    String nickname1 = "nickname1";
    String password = "password";
    String email1 = "email1";
    String jwtAccessToken;


    @BeforeEach
    public void setup(RestDocumentationContextProvider restDocumentationContextProvider) throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController)
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
        Users users2 = Users.builder()
                        .nickname(nickname1)
                        .password(password)
                        .email(email1)
                        .build();
        System.out.println("유저 등록 성공 = " + users2.getId());
        doReturn(users2)
                .when(usersService2).save(any(CreateUserRequest.class));

        // when

        // then
        mockMvc.perform(post("/api/v1/webrtc/chat/register")
                    .content(StrRequest)
                    .contentType(APPLICATION_JSON)
                    .accept(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("register-post",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),

                    requestFields(
                            fieldWithPath("nickname").type(STRING).description("회원 Nickname"),
                            fieldWithPath("password").type(STRING).description("회원 Password"),
                            fieldWithPath("email").type(STRING).description("회원 Email")
                    ),
                    responseFields(
                            fieldWithPath("id").type(STRING).description("회원 PK"),
                            fieldWithPath("email").type(STRING).description("회원 Email"),
                            fieldWithPath("nickname").type(STRING).description("회원 Nickname"),
                            fieldWithPath("created_at").type(NUMBER).description("회원 Nickname"),
                            fieldWithPath("updated_at").type(NUMBER).description("회원 Nickname"),
                            fieldWithPath("birthdate").type(NUMBER).description("회원 Nickname"),
                            fieldWithPath("phone_number").type(STRING).description("회원 Nickname"),
                            fieldWithPath("school").type(STRING).description("회원 Nickname"),
                            fieldWithPath("company").type(STRING).description("회원 Nickname"),
                            fieldWithPath("nickname_expire_at").type(NUMBER).description("회원 Nickname")

                            )
            ));
    }

    @Test
    @Transactional
    public void 토큰발급성공() throws Exception {
        // given
        JwtRequest ObjRequest = new JwtRequest(email1,password);
        String StrRequest = objectMapper.writeValueAsString(ObjRequest);

        doReturn(new org.springframework.security.core.userdetails.User(email1, new BCryptPasswordEncoder().encode(password), new ArrayList<>()))
                .when(jwtUserDetailsService).loadUserByUsername(any(String.class));

        doReturn(true)
                .when(passwordEncoder).matches(any(String.class), any(String.class));

        // when

        // then
        mockMvc.perform(post("/api/v1/webrtc/chat/authenticate")
                        .content(StrRequest)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("authenticate-post-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

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
        Users users2 = Users.builder()
                .nickname(nickname1)
                .password(password)
                .email(email1)
                .build();

        doThrow(new NotExistUserException())
                .when(jwtUserDetailsService).loadUserByUsername(any(String.class));


        // when

        // then
        mockMvc.perform(post("/api/v1/webrtc/chat/authenticate")
                        .content(StrRequest)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(404))
                .andDo(document("authenticate-post-fail-notexistuser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),

                        requestFields(
                                fieldWithPath("email").type(STRING).description("회원 Email"),
                                fieldWithPath("password").type(STRING).description("회원 Password")
                        )
                ));
    }


}
