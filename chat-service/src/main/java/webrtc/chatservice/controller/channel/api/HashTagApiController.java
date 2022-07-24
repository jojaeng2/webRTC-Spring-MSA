package webrtc.chatservice.controller.channel.api;

import com.sun.istack.NotNull;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.chatservice.domain.Channel;
import webrtc.chatservice.dto.ChannelDto;
import webrtc.chatservice.dto.ChannelDto.ChannelResponse;
import webrtc.chatservice.dto.HashTagDto.HashTagResponse;
import webrtc.chatservice.service.channel.ChannelService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webrtc/chat")
public class HashTagApiController {

    private final ChannelService channelService;

    @ApiOperation(value = "해시태그로 채널 목록 반환", notes =
            "같은 hashtag를 가진 채널 목록을 불러옵니다. \n" +
            "채널 목록은 스크롤 페이징을 적용했습니다. 따라서 request를 보낼때 몇번째 페이지에 대한 요청인지 {index} 값을 넣어서 보내주셔야 합니다.\n" +

            "1. header에 jwt access 토큰을 넣어야 합니다. \n" +
            "2. request URL에 hashTag value를 넣어야 합니다. \n" +
            "3. PathParam에 index 값을 넣어주세요. \n\n" +
            "한번에 모든 채널을 불러오지 않고, 페이징 기능을 넣은 이유는 아래와 같습니다.\n" +
            "- 한번에 모든 채널을 불러오는 요청을 처리함으로써 request 처리 비용이 증가합니다. (ex : 서버 메모리 부하, DB 부하, 네트워크 부하 증가로 response 받는 시간 증가 등 )\n" +
            "- channel에는 TTL이 존재합니다. TTL은 DB에 따로 저장하지 않고 response를 보내기 직전 처리합니다. 따라서 불러온 데이터의 양에따라 처리 시간이 증가합니다. \n" +
            "- 사용자의 가독성이 떨어집니다. \n"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "tagName"
                    , value = "검색에 사용할 Tag Name입니다."
                    , required = true
                    , dataType = "string"
                    , paramType = "path"
            ),
            @ApiImplicitParam(
                    name = "idx"
                    , value = "스크롤 페이징을 위한 index 값입니다."
                    , required = true
                    , dataType = "string"
                    , paramType = "path"
            )
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "채널 목록을 정상적으로 생성합니다."),
            @ApiResponse(code = 401, message = "jwt token에 아래의 4가지 문제중 하나가 존재합니다. 재로그인 후 토큰을 다시 발급받아 사용해야합니다. \n" +
                    "1. 존재하지 않는 User token입니다. \n" +
                    "2. Jwt Access Token이 만료되었습니다.\n" +
                    "3. 올바르지 않은 Jwt Token 형식입니다. \n" +
                    "4. 올바르지 않은 Jwt Hashing algorithm입니다."

            )
    })
    @GetMapping("/hashtag/{tagName}/{orderType}/{idx}")
    public ResponseEntity<HashTagResponse> searchHashTag(@NotNull @PathVariable("orderType") String orderType, @PathVariable String tagName, @PathVariable("idx") String idx) {
        List<ChannelResponse> channels = channelService.findChannelByHashName(tagName, orderType, Integer.parseInt(idx));
        return new ResponseEntity<>(new HashTagResponse(channels), HttpStatus.OK);
    }
}
