package webrtc.voiceservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import webrtc.voiceservice.domain.User;
import webrtc.voiceservice.dto.UserDto.FindUserByEmailRequest;
import webrtc.voiceservice.exception.UserException.NotExistUserException;
import webrtc.voiceservice.util.CustomJsonMapper;

@RestController
@RequiredArgsConstructor
public class HttpApiController {

    private final CustomJsonMapper customJsonMapper;

    RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<String> getRequest(String url) {
        return restTemplate.getForEntity(url, String.class);
    }

    public ResponseEntity<String> postRequest(String url, Object object) {
        HttpEntity<Object> request = new HttpEntity<>(object);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return response;
    }

    public User postFindUserByEmail(String email) {
        try {
            ResponseEntity<String> response = postRequest("http://localhost:81/api/v1/webrtc/user", new FindUserByEmailRequest(email));
            String responseBody = response.getBody();
            Object obj = customJsonMapper.jsonParse(responseBody, User.class);
            return User.class.cast(obj);
        } catch (HttpClientErrorException e) {
            throw new NotExistUserException();
        }
    }
}
