package webrtc.chatservice.controller.point;


import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.chatservice.dto.ChannelDto.ExtensionChannelTTLRequest;
import webrtc.chatservice.dto.ChannelDto.ExtensionChannelTTLResponse;
import webrtc.chatservice.service.channel.ChannelService;
import webrtc.chatservice.service.user.UserService;
import webrtc.chatservice.utils.JwtTokenUtil;

import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webrtc/chat")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PointApiController {

    private final ChannelService channelService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;



    @PostMapping("/extension/{id}")
    public ResponseEntity<ExtensionChannelTTLResponse> extensionChannelTTL(@RequestBody ExtensionChannelTTLRequest request, @PathVariable("id") String channelId, @RequestHeader("Authorization")String jwtAccessToken) {
        String userEmail = jwtTokenUtil.getUserEmailFromToken(jwtAccessToken.substring(4));
        Long requestTTL = request.getRequestTTL();
        channelService.extensionChannelTTL(channelId, userEmail, requestTTL);
        return new ResponseEntity<>(new ExtensionChannelTTLResponse(channelService.findOneChannelById(channelId).getTimeToLive()), OK);
    }


    @ApiOperation(value = "채널의 수명과 보유한 포인트를 확인합니다.", notes =
            "1. header에 jwt access 토큰을 넣어주세요 합니다. \n" +
            "2. channelId를 PathParam으로 넣어주세요. \n"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "id"
                    , value = "남은 시간을 검색할 채널 id 입니다."
                    , required = true
                    , dataType = "string"
                    , paramType = "path"
            )
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "채널의 TTL과 User가 보유한 point를 정상적으로 반환합니다."),
            @ApiResponse(code = 401, message = "jwt token에 아래의 4가지 문제중 하나가 존재합니다. 재로그인 후 토큰을 다시 발급받아 사용해야합니다. \n" +
                    "1. 존재하지 않는 User token입니다. \n" +
                    "2. Jwt Access Token이 만료되었습니다.\n" +
                    "3. 올바르지 않은 Jwt Token 형식입니다. \n" +
                    "4. 올바르지 않은 Jwt Token 형식입니다."
            ),
            @ApiResponse(code = 404, message = "같은 이름의 channelName이 이미 존재합니다.")
    })
    @GetMapping("/point/{id}")
    public ResponseEntity<?> findUserPoint(@PathVariable("id") String channelId, @RequestHeader("Authorization")String jwtAccessToken) {
        String userEmail = jwtTokenUtil.getUserEmailFromToken(jwtAccessToken.substring(4));
        return new ResponseEntity<>(userService.findUserWithPointByEmail(channelId, userEmail), OK);
    }
}