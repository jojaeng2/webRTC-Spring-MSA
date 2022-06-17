package webrtc.chatservice.controller.channel.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import webrtc.chatservice.dto.ChannelDto.CreateChannelRequest;
import webrtc.chatservice.dto.HashTagDto.HashTagResponse;
import webrtc.chatservice.dto.JwtDto.JwtRequest;
import webrtc.chatservice.dto.JwtDto.JwtResponse;
import webrtc.chatservice.dto.UserDto.CreateUserRequest;
import webrtc.chatservice.utils.CustomJsonMapper;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class HashTagApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private CustomJsonMapper customJsonMapper;

    @Autowired
    private ObjectMapper objectMapper;


    private String jwtAccessToken;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .build();

        ResultActions resultActions0 = mockMvc.perform(post("/api/v1/webrtc/register")
                .content(new ObjectMapper().writeValueAsString(CreateUserRequest("user")))
                .contentType(APPLICATION_JSON));


        ResultActions resultActions2 = mockMvc.perform(post("/api/v1/webrtc/authenticate")
                .content(new ObjectMapper().writeValueAsString(CreateJwtAccessTokenRequest()))
                .contentType(APPLICATION_JSON));

        Object object = customJsonMapper.jsonParse(resultActions2.andReturn().getResponse().getContentAsString(), JwtResponse.class);
        jwtAccessToken = JwtResponse.class.cast(object).getJwttoken();
    }

    @Test
    @DisplayName("HashTagName으로 Channel들 검색")
    public void SearchChannelsByHashTagName() throws Exception {
        // given
        String channelName = "testChannel";
        CreateChannelRequest request = CreateChannelRequest(channelName);
        mockMvc.perform(post("/api/v1/webrtc/channel").header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON));

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/webrtc/hashtag/testTag1")
                .header(HttpHeaders.AUTHORIZATION, "jwt " + jwtAccessToken));
        Object obj = customJsonMapper.jsonParse(resultActions.andReturn().getResponse().getContentAsString(), HashTagResponse.class);
        HashTagResponse response = HashTagResponse.class.cast(obj);

        // then
        resultActions.andExpect(status().isOk());
        assertThat(response.getChannels().get(0).getChannelName()).isEqualTo(channelName);
    }

    private JwtRequest CreateJwtAccessTokenRequest() {
        return new JwtRequest("email", "user");
    }

    private CreateUserRequest CreateUserRequest(String userName) {
        return new CreateUserRequest(userName, "user", "email");
    }

    private CreateChannelRequest CreateChannelRequest(String channelName) {
        List<String> hashTags = new ArrayList<>();
        hashTags.add("testTag1");
        hashTags.add("testTag2");
        hashTags.add("testTag3");
        return new CreateChannelRequest(channelName, hashTags);
    }
}
