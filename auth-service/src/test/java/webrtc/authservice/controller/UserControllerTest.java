package webrtc.authservice.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import webrtc.authservice.dto.UserDto;
import webrtc.authservice.dto.UserDto.CreateUserRequest;
import webrtc.authservice.service.UserService;

@AutoConfigureRestDocs
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .build();
    }

    @Test
    public void test_새로운_User를_등록한다_성공() throws Exception {

        // given
        CreateUserRequest request = new CreateUserRequest("nickname", "password", "email");

        String requestToStr = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/webrtc/auth/register")
                .content(requestToStr)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(document("auth-service",
                    requestFields(
                            fieldWithPath("nickname").type(STRING).description("회원 nickname"),
                            fieldWithPath("password").type(STRING).description("회원 password"),
                            fieldWithPath("email").type(STRING).description("회원 email")
                    ),
                    responseFields(
                            fieldWithPath("id").type(STRING).description("회원 PK"),
                            fieldWithPath("created_at").type(null).description("회원 생성 시간"),
                            fieldWithPath("updated_at").type(null).description("회원 정보 업데이트 시간"),
                            fieldWithPath("email").type(STRING).description("회원 email"),
                            fieldWithPath("password").type(STRING).description("회원 password"),
                            fieldWithPath("birthdate").type(null).description("회원 생일"),
                            fieldWithPath("phone_number").type(null).description("회원 휴대폰 번호"),
                            fieldWithPath("school").type(null).description("회원 학교 정보"),
                            fieldWithPath("company").type(null).description("회원 회사 정보"),
                            fieldWithPath("nickname").type(null).description("회원 닉네임"),
                            fieldWithPath("nickname_expire_at").type(null).description("회원 닉네임 만료시간")

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
}