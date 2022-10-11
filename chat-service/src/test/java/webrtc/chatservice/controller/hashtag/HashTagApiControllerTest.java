package webrtc.chatservice.controller.hashtag;

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
import webrtc.chatservice.domain.ChannelHashTag;
import webrtc.chatservice.domain.HashTag;
import webrtc.chatservice.dto.ChannelDto.ChannelResponse;
import webrtc.chatservice.enums.ChannelType;
import webrtc.chatservice.exception.HashTagException.NotExistHashTagException;
import webrtc.chatservice.service.channel.ChannelFindService;
import webrtc.chatservice.service.jwt.JwtUserDetailsService;
import webrtc.chatservice.service.users.UsersService;
import webrtc.chatservice.utils.jwt.JwtTokenUtilImpl;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static webrtc.chatservice.enums.ChannelType.TEXT;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@ExtendWith(MockitoExtension.class)
public class HashTagApiControllerTest {


    @InjectMocks
    private HashTagApiController hashTagApiController;
    @Spy
    private JwtTokenUtilImpl jwtTokenUtil;
    @Mock
    private JwtUserDetailsService jwtUserDetailsService;
    @Mock
    private ChannelFindService channelFindService;
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
        this.mockMvc = MockMvcBuilders.standaloneSetup(hashTagApiController)
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
    public void 해시태그채널목록_불러오기성공() throws Exception {
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
                .when(channelFindService).findChannelByHashName(any(String.class), any(String.class), any(Integer.class));

        // when

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/webrtc/chat/hashtag/{tagName}/{orderType}/{idx}",
                                tag1, "partiDESC", "0")
                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(200))
                .andDo(document("return-hashtagchannels-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("tagName").description("채널 목록을 찾는데 사용되는 해시태그입니다."),
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
    public void 해시태그채널목록_불러오기실패() throws Exception {
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

        doThrow(new NotExistHashTagException())
                .when(channelFindService).findChannelByHashName(any(String.class), any(String.class), any(Integer.class));

        // when

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/webrtc/chat/hashtag/{tagName}/{orderType}/{idx}",
                                tag1, "partiDESC", "0")
                        .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().is(404))
                .andDo(document("return-hashtagchannels-fail-notExistHashtag",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("tagName").description("채널 목록을 찾는데 사용되는 해시태그입니다."),
                                parameterWithName("orderType").description("채널 목록을 불러올 정렬 기준입니다."),
                                parameterWithName("idx").description("몇번째 채널 목록을 불러올지 알려주는 index 값입니다.")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Jwt Access 토큰")
                        )
                ));
    }
}
