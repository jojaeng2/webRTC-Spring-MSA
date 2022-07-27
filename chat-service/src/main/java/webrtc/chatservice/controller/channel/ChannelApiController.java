package webrtc.chatservice.controller.channel;

import com.sun.istack.NotNull;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.dto.ChannelDto.*;
import webrtc.chatservice.service.channel.ChannelService;
import webrtc.chatservice.utils.JwtTokenUtil;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webrtc/chat")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChannelApiController {

    private final ChannelService channelService;

    private final JwtTokenUtil jwtTokenUtil;

    @ApiOperation(value = "채널 생성", notes =
            " \n" +
            "2. \n" +
            "3.  \n" +
            "4.  "
    )
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "channelName"
            , value = "생성할 채널 이름입니다"
            , required = true
            , dataType = "string"
            , paramType = "query"
        ),
        @ApiImplicitParam(
                name = "hashTags"
                , value = "채널에 사용될 해시태그들 입니다"
                , required = true
                , dataType = "array"
                , paramType = "query"
        ),
        @ApiImplicitParam(
                name = "channelType"
                , value = "생성될 채널의 타입입니다"
                , required = true
                , dataType = "string"
                , paramType = "query"
        )
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "채널을 정상적으로 생성합니다."),
            @ApiResponse(code = 401, message = "jwt token에 아래의 4가지 문제중 하나가 존재합니다. 재로그인 후 토큰을 다시 발급받아 사용해야합니다. \n" +
                    "1. 존재하지 않는 User token입니다. \n" +
                    "2. Jwt Access Token이 만료되었습니다.\n" +
                    "3. 올바르지 않은 Jwt Token 형식입니다. \n" +
                    "4. 올바르지 않은 Jwt Hashing algorithm입니다."
            ),
            @ApiResponse(code = 409, message = "같은 이름의 channelName이 이미 존재합니다.")
    })
    @PostMapping("/channel")
    public ResponseEntity<CreateChannelResponse> createChannel(@RequestBody CreateChannelRequest request, @RequestHeader("Authorization") String jwtAccessToken) {
        String userEmail = jwtTokenUtil.getUserEmailFromToken(jwtAccessToken.substring(4));
        Channel channel = channelService.createChannel(request, userEmail);
        return new ResponseEntity<>(new CreateChannelResponse(channel.getChannelName(), channel.getLimitParticipants(), channel.getCurrentParticipants(), channel.getTimeToLive()), HttpStatus.OK);
    }


    @ApiOperation(value = "채널 목록을 반환", notes =
            "채널 목록은 스크롤 페이징을 적용했습니다. 따라서 request를 보낼때 몇번째 페이지에 대한 요청인지 {index} 값을 넣어서 보내주셔야 합니다.\n" +
            "1. header에 jwt access 토큰을 넣어야 합니다. \n" +
            "2. PathParam에 index 값을 넣어주세요. \n\n" +

            "한번에 모든 채널을 불러오지 않고, 페이징 기능을 넣은 이유는 아래와 같습니다.\n" +
            "- 한번에 모든 채널을 불러오는 요청을 처리함으로써 request 처리 비용이 증가합니다. (ex : 서버 메모리 부하, DB 부하, 네트워크 부하 증가로 response 받는 시간 증가 등 )\n" +
            "- channel에는 TTL이 존재합니다. TTL은 DB에 따로 저장하지 않고 response를 보내기 직전 처리합니다. 따라서 불러온 데이터의 양에따라 처리 시간이 증가합니다. \n" +
            "- 사용자의 가독성이 떨어집니다. \n"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "idx"
                    , value = "스크롤 페이징을 위한 index 값입니다."
                    , required = true
                    , dataType = "string"
                    , paramType = "path"
            )
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "채널목록을 정상적으로 반환합니다."),
            @ApiResponse(code = 401, message = "jwt token에 아래의 4가지 문제중 하나가 존재합니다. 재로그인 후 토큰을 다시 발급받아 사용해야합니다. \n" +
                    "1. 존재하지 않는 User token입니다. \n" +
                    "2. Jwt Access Token이 만료되었습니다.\n" +
                    "3. 올바르지 않은 Jwt Token 형식입니다. \n" +
                    "4. 올바르지 않은 Jwt Hashing algorithm입니다."
            ),
    })
    @GetMapping("/channels/{orderType}/{idx}")
    public ResponseEntity<FindAllChannelResponse> findAnyChannel(@NotNull @PathVariable("orderType") String orderType, @NotNull @PathVariable("idx") String idx) {
        List<ChannelResponse> channels = channelService.findAnyChannel(orderType, Integer.parseInt(idx));
        return new ResponseEntity<>(new FindAllChannelResponse(channels), HttpStatus.OK);
    }


    @ApiOperation(value = "사용자가 입장하고 있는 채널 목록을 반환", notes =
            "jwt token으로 알아서 사용자를 구별하고, 해당 사용자가 입장한 채널 정보들을 넘겨줍니다. \n" +
            "client에 따라 interface를 구별하여 구현했기 때문에 일반 채널 목록 불러오기와 url을 제외한 모든 요청 형식이 같습니다. \n" +
            "채널 목록은 스크롤 페이징을 적용했습니다. 따라서 request를 보낼때 몇번째 페이지에 대한 요청인지 {index} 값을 넣어서 보내주셔야 합니다.\n" +
                    "1. header에 jwt access 토큰을 넣어야 합니다. \n" +
                    "2. PathParam에 index 값을 넣어주세요. \n\n" +

            "한번에 모든 채널을 불러오지 않고, 페이징 기능을 넣은 이유는 아래와 같습니다.\n" +
            "- 한번에 모든 채널을 불러오는 요청을 처리함으로써 request 처리 비용이 증가합니다. (ex : 서버 메모리 부하, DB 부하, 네트워크 부하 증가로 response 받는 시간 증가 등 )\n" +
            "- channel에는 TTL이 존재합니다. TTL은 DB에 따로 저장하지 않고 response를 보내기 직전 처리합니다. 따라서 불러온 데이터의 양에따라 처리 시간이 증가합니다. \n" +
            "- 사용자의 가독성이 떨어집니다. \n"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "idx"
                    , value = "스크롤 페이징을 위한 index 값입니다."
                    , required = true
                    , dataType = "string"
                    , paramType = "path"
            )
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "채널목록을 정상적으로 반환합니다."),
            @ApiResponse(code = 401, message = "jwt token에 아래의 4가지 문제중 하나가 존재합니다. 재로그인 후 토큰을 다시 발급받아 사용해야합니다. \n" +
                    "1. 존재하지 않는 User token입니다. \n" +
                    "2. Jwt Access Token이 만료되었습니다.\n" +
                    "3. 올바르지 않은 Jwt Token 형식입니다. \n" +
                    "4. 올바르지 않은 Jwt Hashing algorithm입니다."
            )
    })
    @GetMapping("/mychannel/{orderType}/{idx}")
    public ResponseEntity<FindAllChannelResponse> findMyAllChannel(@NotNull @PathVariable("orderType") String orderType, @NotNull @RequestHeader("Authorization") String jwtAccessToken, @NotNull @PathVariable("idx") String idx) {
        String userEmail = jwtTokenUtil.getUserEmailFromToken(jwtAccessToken.substring(4));
        List<ChannelResponse> channels = channelService.findMyChannel(orderType, userEmail, Integer.parseInt(idx));
        return new ResponseEntity<>(new FindAllChannelResponse(channels), OK);
    }


    @ApiOperation(value = "특정 채널정보 반환", notes =
            "1. header에 jwt access 토큰을 넣어야 합니다. \n" +
            "2. PathParameter로 channelId를 추가합니다."
    )
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "channelId"
                    , value = "반환할 채널의 channelId입니다."
                    , required = true
                    , dataType = "string"
                    , paramType = "path"
            )
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "채널을 정상적으로 반환합니다."),
            @ApiResponse(code = 401, message = "jwt token에 아래의 4가지 문제중 하나가 존재합니다. 재로그인 후 토큰을 다시 발급받아 사용해야합니다. \n" +
                    "1. 존재하지 않는 User token입니다. \n" +
                    "2. Jwt Access Token이 만료되었습니다.\n" +
                    "3. 올바르지 않은 Jwt Token 형식입니다. \n" +
                    "4. 올바르지 않은 Jwt Hashing algorithm입니다."
            ),
            @ApiResponse(code = 404, message = "존재하지 않는 채널입니다.")
    })
    @GetMapping("/channel/{id}")
    public ResponseEntity<ChannelResponse> findOneChannel(@PathVariable("id") String channelId) {
        Channel channel = channelService.findOneChannelById(channelId);
        return new ResponseEntity<>(new ChannelResponse(channelId, channel.getChannelName(), channel.getLimitParticipants(), channel.getCurrentParticipants(), channel.getTimeToLive(), channel.getChannelHashTags(), channel.getChannelType()), OK);
    }
}
