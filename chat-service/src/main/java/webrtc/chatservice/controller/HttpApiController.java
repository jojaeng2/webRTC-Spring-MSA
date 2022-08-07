package webrtc.chatservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import webrtc.chatservice.domain.Users;
import webrtc.chatservice.dto.UsersDto.*;
import webrtc.chatservice.exception.PointException.InsufficientPointException;
import webrtc.chatservice.exception.UserException.NotExistUserException;
import webrtc.chatservice.utils.CustomJsonMapper;

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
        return restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }

    public Users postFindUserByEmail(String email) {
        try {
            ResponseEntity<String> response = postRequest("http://auth-service:8080/api/v1/webrtc/auth/user", new FindUserByEmailRequest(email));
            String responseBody = response.getBody();
            Object obj = customJsonMapper.jsonParse(responseBody, Users.class);
            return Users.class.cast(obj);
        } catch (HttpClientErrorException e) {
            switch (e.getStatusCode()) {
                case NOT_FOUND:
                    throw new NotExistUserException();
            }
        }
        return new Users();
    }

    public FindUserWithPointByEmailResponse postFindUserWithPointByEmail(String email) {
        try {
            ResponseEntity<String> response = postRequest("http://auth-service:8080/api/v1/webrtc/auth/user/point", new FindUserByEmailRequest(email));
            String responseBody = response.getBody();
            Object obj = customJsonMapper.jsonParse(responseBody, FindUserWithPointByEmailResponse.class);
            return FindUserWithPointByEmailResponse.class.cast(obj);
        } catch (HttpClientErrorException e) {
            switch (e.getStatusCode()) {
                case NOT_FOUND:
                    throw new NotExistUserException();
            }
        }
        return new FindUserWithPointByEmailResponse();
    }

    public void postDecreaseUserPoint(String email, Long point) {
        try {
            ResponseEntity<String> response = postRequest("http://auth-service:8080/api/v1/webrtc/auth/decrease/point", new DecreasePointRequest(email, point.intValue()));
        } catch (HttpClientErrorException e) {
            switch (e.getStatusCode()) {
                case NOT_FOUND:
                    throw new NotExistUserException();
                case CONFLICT:
                    throw new InsufficientPointException();
            }
        }
    }

    public Users postSaveUser(CreateUserRequest request) {
        String response = postRequest("http://auth-service:8080/api/v1/webrtc/auth/register", request).getBody();
        Object obj = customJsonMapper.jsonParse(response, Users.class );
        return Users.class.cast(obj);
    }

    public void postDeletedChannel(String channelId) {
        String response = postRequest("http://voice-service:8080/api/v1/webrtc/voice/remove-channel", channelId).getBody();
    }


}
