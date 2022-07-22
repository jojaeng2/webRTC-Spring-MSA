package webrtc.voiceservice.controller.session;


import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.voiceservice.controller.HttpApiController;
import webrtc.voiceservice.domain.User;
import webrtc.voiceservice.dto.SessionDto.GetTokenRequest;
import webrtc.voiceservice.dto.SessionDto.GetTokenResponse;
import webrtc.voiceservice.dto.SessionDto.RemoveUserInSessionRequest;
import webrtc.voiceservice.service.session.OpenViduSessionService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/webrtc/voice")
@RequiredArgsConstructor
public class SessionController {

    private final OpenViduSessionService openViduSessionService;
    private final HttpApiController httpApiController;


    @ApiOperation(value = "openvidu token 발급", notes =
                    "jwt Access token을 발급 받습니다. \n" +
                    "1. 아직 토큰을 발급받지 않은 상태이니, Header에는 별도의 처리를 안하셔도 됩니다.\n" +
                    "2. email과 password로 가입된 사용자인지 확인합니다.\n"
    )
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "email"
            , value = "로그인에 사용할 email 입니다."
            , required = true
            , dataType = "string"
            , paramType = "query"
        ),
        @ApiImplicitParam(
                name = "password"
                , value = "로그인에 사용할 password입니다."
                , required = true
                , dataType = "string"
                , paramType = "query"
        )
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "토큰을 정상적으로 발급합니다."),
            @ApiResponse(code = 404, message = "로그인 정보가 잘못되었습니다.")
    })
    @PostMapping("/get-token")
    public ResponseEntity<?> getToken(@RequestBody GetTokenRequest request) {
        User user = httpApiController.postFindUserByEmail(request.getEmail());
        String token = openViduSessionService.createToken(request, user);
        return new ResponseEntity(new GetTokenResponse(token), HttpStatus.OK);
    }

    @PostMapping("/remove-user")
    public ResponseEntity<?> removeUser(@RequestBody RemoveUserInSessionRequest request) throws Exception{

        String email = request.getEmail();
        User user = httpApiController.postFindUserByEmail(email);
        openViduSessionService.removeUserInOpenViduSession(request, user);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/remove-channel")
    public ResponseEntity<?> removeChannel(@RequestBody String channelId) {

        openViduSessionService.deletedChannel(channelId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
