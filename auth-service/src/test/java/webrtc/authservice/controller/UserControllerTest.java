package webrtc.authservice.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import webrtc.authservice.dto.UserDto.CreateUserRequest;
import webrtc.authservice.dto.UserDto.DecreasePointRequest;
import webrtc.authservice.dto.UserDto.FindUserByEmailRequest;
import webrtc.authservice.dto.UserDto.FindUserWithPointByEmailRequest;
import webrtc.authservice.service.user.UserService;

@AutoConfigureRestDocs
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    String email = "email";
    String nickname = "nickname";
    String password = "password";
    int welcomePoint = 1000000;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // ?????? ??????
                .build();
    }

    @BeforeEach
    public void flushRedis() {
        userService.redisDataEvict();
    }

    @Test
    @Transactional
    public void ?????????_User??????_??????() throws Exception {

        // given
        CreateUserRequest request = new CreateUserRequest(nickname, password, email);

        String requestToStr = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/webrtc/auth/register")
                .content(requestToStr)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("auth-service",
                    requestFields(
                            fieldWithPath("nickname").type(STRING).description("?????? nickname"),
                            fieldWithPath("password").type(STRING).description("?????? password"),
                            fieldWithPath("email").type(STRING).description("?????? email")
                    ),
                    responseFields(
                            fieldWithPath("id").type(STRING).description("?????? PK"),
                            fieldWithPath("created_at").type(null).description("?????? ?????? ??????"),
                            fieldWithPath("updated_at").type(null).description("?????? ?????? ???????????? ??????"),
                            fieldWithPath("email").type(STRING).description("?????? email"),
                            fieldWithPath("password").type(STRING).description("?????? password"),
                            fieldWithPath("birthdate").type(null).description("?????? ??????"),
                            fieldWithPath("phone_number").type(null).description("?????? ????????? ??????"),
                            fieldWithPath("school").type(null).description("?????? ?????? ??????"),
                            fieldWithPath("company").type(null).description("?????? ?????? ??????"),
                            fieldWithPath("nickname").type(null).description("?????? ?????????"),
                            fieldWithPath("nickname_expire_at").type(null).description("?????? ????????? ????????????")

                    )
                ))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.created_at", is(nullValue())))
                .andExpect(jsonPath("$.updated_at", is(nullValue())))
                .andExpect(jsonPath("$.email", is("email")))
                .andExpect(jsonPath("$.password").exists())
                .andExpect(jsonPath("$.birthdate", is(nullValue())))
                .andExpect(jsonPath("$.phone_number", is(nullValue())))
                .andExpect(jsonPath("$.school", is(is(nullValue()))))
                .andExpect(jsonPath("$.company", is(is(nullValue()))))
                .andExpect(jsonPath("$.nickname", is("nickname")))
                .andExpect(jsonPath("$.nickname_expire_at", is(nullValue())))
                .andDo(document("auth-service"));
    }



    @Test
    @Transactional
    public void User??????_??????_??????() throws Exception {

        // given
        CreateUserRequest createRequest = new CreateUserRequest(nickname, password, email);

        String createRequestToStr = objectMapper.writeValueAsString(createRequest);
        mockMvc.perform(post("/api/v1/webrtc/auth/register")
                .content(createRequestToStr)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // when
        FindUserByEmailRequest findRequest = new FindUserByEmailRequest(email);

        String findRequestToStr = objectMapper.writeValueAsString(findRequest);
        mockMvc.perform(post("/api/v1/webrtc/auth/user")
                .content(findRequestToStr)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Transactional
    public void User??????_??????_??????() throws Exception {
        // given

        // when
        FindUserByEmailRequest findRequest = new FindUserByEmailRequest(email);

        String findRequestToStr = objectMapper.writeValueAsString(findRequest);
        mockMvc.perform(post("/api/v1/webrtc/auth/user")
                        .content(findRequestToStr)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(404));
    }

    @Test
    @Transactional
    public void User??????_with?????????_????????????() throws Exception {
        // given
        CreateUserRequest createRequest = new CreateUserRequest(nickname, password, email);

        String createRequestToStr = objectMapper.writeValueAsString(createRequest);
        mockMvc.perform(post("/api/v1/webrtc/auth/register")
                .content(createRequestToStr)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // when
        FindUserWithPointByEmailRequest findRequest = new FindUserWithPointByEmailRequest(email);

        String findRequestToStr = objectMapper.writeValueAsString(findRequest);
        mockMvc.perform(post("/api/v1/webrtc/auth/user/point")
                        .content(findRequestToStr)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.nickname", is(nickname)))
                .andExpect(jsonPath("$.point", is(welcomePoint)));
    }


    @Test
    @Transactional
    public void User??????_with?????????_????????????() throws Exception {
        // given

        // when
        FindUserByEmailRequest findRequest = new FindUserByEmailRequest(email);

        String findRequestToStr = objectMapper.writeValueAsString(findRequest);
        mockMvc.perform(post("/api/v1/webrtc/auth/user/point")
                        .content(findRequestToStr)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(404));
    }

    @Test
    @Transactional
    public void User?????????_??????_??????() throws Exception {
        // given
        CreateUserRequest createRequest = new CreateUserRequest(nickname, password, email);

        String createRequestToStr = objectMapper.writeValueAsString(createRequest);
        mockMvc.perform(post("/api/v1/webrtc/auth/register")
                .content(createRequestToStr)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // when
        DecreasePointRequest decreaseRequest = new DecreasePointRequest(email, welcomePoint/2);

        String decreaseRequestToStr = objectMapper.writeValueAsString(decreaseRequest);
        mockMvc.perform(post("/api/v1/webrtc/auth/decrease/point")
                        .content(decreaseRequestToStr)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());


        FindUserByEmailRequest findRequest = new FindUserByEmailRequest(email);
        String findRequestToStr = objectMapper.writeValueAsString(findRequest);
        mockMvc.perform(post("/api/v1/webrtc/auth/user/point")
                        .content(findRequestToStr)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.nickname", is(nickname)))
                .andExpect(jsonPath("$.point", is(welcomePoint/2)));
    }


    @Test
    @Transactional
    public void User?????????_??????_??????() throws Exception {
        // given
        CreateUserRequest createRequest = new CreateUserRequest(nickname, password, email);

        String createRequestToStr = objectMapper.writeValueAsString(createRequest);
        mockMvc.perform(post("/api/v1/webrtc/auth/register")
                .content(createRequestToStr)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON));

        // when
        DecreasePointRequest decreaseRequest = new DecreasePointRequest(email, welcomePoint*2);

        String decreaseRequestToStr = objectMapper.writeValueAsString(decreaseRequest);
        mockMvc.perform(post("/api/v1/webrtc/auth/decrease/point")
                        .content(decreaseRequestToStr)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(409));
    }
}