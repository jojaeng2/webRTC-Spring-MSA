package webrtc.chatservice.controller.chat;

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
import webrtc.chatservice.controller.HttpApiController;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.domain.ChatLog;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.service.chat.ChatLogService;
import webrtc.chatservice.service.jwt.JwtUserDetailsService;
import webrtc.chatservice.service.users.UsersService;
import webrtc.chatservice.utils.jwt.JwtTokenUtilImpl;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static webrtc.chatservice.enums.ChannelType.TEXT;
import static webrtc.chatservice.enums.ClientMessageType.CHAT;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@ExtendWith(MockitoExtension.class)
public class ChatLogApiControllerTest {

    @InjectMocks
    private ChatLogApiController chatLogApiController;
    @Spy
    private JwtTokenUtilImpl jwtTokenUtil;
    @Mock
    private JwtUserDetailsService jwtUserDetailsService;
    @Mock
    private ChatLogService chatLogService;
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
        hashTagList.add(tag1);
        hashTagList.add(tag2);
        hashTagList.add(tag3);
        this.mockMvc = MockMvcBuilders.standaloneSetup(chatLogApiController)
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
    public void 채팅로그_불러오기성공() throws Exception {
        // given
        Channel channel = Channel.builder()
                .channelName(channelName1)
                .channelType(text)
                .build();
        List<ChatLog> chatLogList = new ArrayList<>();

        Long testCase = 39L;
        for(Long i=19L; i<testCase; i++) {
            ChatLog chatLog = ChatLog.builder()
                    .type(CHAT)
                    .message(i+" 번째 메시지")
                    .senderNickname(nickname1)
                    .senderEmail(email1)
                    .build();
            chatLog.setChatLogIdx(i);
            chatLogList.add(chatLog);
        }

        doReturn(chatLogList)
                .when(chatLogService).findChatLogsByIndex(channel.getId(), testCase);

        // when

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/webrtc/chat/channel/{id}/{idx}", channel.getId(), testCase)
                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("return-chatLog-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("채널ID입니다."),
                                parameterWithName("idx").description("마지막으로 받은 채팅로그 idx 값입니다.")
                                ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Jwt Access 토큰")
                        ),
                        responseFields(
                                fieldWithPath("logs").type(ARRAY).description("채팅로그 배열"),
                                fieldWithPath("logs[].idx").type(NUMBER).description("채팅로그 순서"),
                                fieldWithPath("logs[].type").type(STRING).description("채팅로그 타입"),
                                fieldWithPath("logs[].message").type(STRING).description("채팅로그 내용"),
                                fieldWithPath("logs[].senderNickname").type(STRING).description("채팅로그 보낸사람 닉네임"),
                                fieldWithPath("logs[].senderEmail").type(STRING).description("채팅로그 보낸사람 이메일"),
                                fieldWithPath("logs[].sendTime").type(NUMBER).description("채팅로그 보낸시간")
                                )
                ));
    }

}
