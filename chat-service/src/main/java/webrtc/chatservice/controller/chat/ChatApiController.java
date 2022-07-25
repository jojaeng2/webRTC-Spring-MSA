package webrtc.chatservice.controller.chat;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.chatservice.dto.ChatDto.FindChatLogsResponse;
import webrtc.chatservice.service.chat.ChatService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webrtc/chat")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChatApiController {

    private final ChatService chatService;

    @ApiOperation(value = "채팅 로그 반환", notes =
            "같은 channel에 전송된 채팅 로그들을 불러옵니다. 채팅 로그는 스크롤 페이징을 적용했습니다.\n" +
            "현재 채팅 로그 DB 스키마는 해당 채팅방에 몇번째로 보내진 로그인지에 대한 column이 있고, 이 값은 채팅로그가 쌓일수록 증가합니다. 이값을 이용해 페이징을 구현하였습니다. \n" +
            "한번에 모든 로그를 불러오지 않고, 페이징 기능을 넣은 이유는 아래와 같습니다.\n" +
            "- 한번에 모든 로그를 불러오는 요청을 처리함으로써 request 처리 비용이 증가합니다. (ex : 서버 메모리 부하, DB 부하, 네트워크 부하 증가로 response 받는 시간 증가 등 )\n" +
            "- 사용자의 가독성이 떨어집니다. \n\n" +
            "현재 웹에서 채팅 로그는 아래와 같이 불러옵니다.   \n" +
            "1. client는 자신이 마지막으로 받은 채팅 로그가 몇번째 로그인지 기억하고, 이를 이용해 요청을 보내도록 구현했습니다. TCP의 ACK 응답과 같은 로직이라고 생각하시면 됩니다. \n" +
            "2. Server는 클라이언트의 요청을 받으면 해당 사용자가 몇번째 로그까지 받았는지 알 수있습니다. \n" +
            "3. Server는 [max(0, idx-(logSize+1)), idx-1]의 로그를 response로 넘겨줍니다.\n" +
            "4. 현재 웹 프론트에서는 list를 deque처럼 사용하며 채팅 로그를 쌓아 관리하며, 새로운 채팅 로그는 배열의 끝에 삽입하고, 새로 불러온 채팅 로그는 배열의 앞에 삽입합니다. 이때 deque 자료구조의 원리를 따르면 O(1)에 로그를 삽입할 수있습니다. \n" +
            "5. 만약 새로운 채팅 로그를 요청해야 한다면, 배열의 가장 앞에있는 로그의 idx를 이용합니다. 이또한 상수시간내에 처리가 가능합니다. \n" +
            "아래는 구현시 고려해야 할 내용입니다. \n" +
            "(1) header에 jwt access 토큰을 넣어주세요. \n" +
            "(2) request URL에 입장한 channelId를 넣어주세요. \n" +
            "(3) PathParam에 index 값을 넣어주세요. \n\n"

    )
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "channelId"
                    , value = "채팅 로그를 검색하기 위해 사용될 channelId 입니다."
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
            @ApiResponse(code = 200, message = "채팅로그 목록을 정상적으로 반환합니다."),
            @ApiResponse(code = 401, message = "jwt token에 아래의 4가지 문제중 하나가 존재합니다. 재로그인 후 토큰을 다시 발급받아 사용해야합니다. \n" +
                    "1. 존재하지 않는 User token입니다. \n" +
                    "2. Jwt Access Token이 만료되었습니다.\n" +
                    "3. 올바르지 않은 Jwt Token 형식입니다. \n" +
                    "4. 올바르지 않은 Jwt Hashing algorithm입니다."
            )
    })
    @GetMapping("/channel/{channelId}/{idx}")
    public ResponseEntity<FindChatLogsResponse> findChatLogs(@PathVariable("channelId") String channelId, @PathVariable("idx") String idx) {
        return new ResponseEntity<>(new FindChatLogsResponse(chatService.findChatLogsByIndex(channelId, Long.parseLong(idx))), HttpStatus.OK);
    }
}
