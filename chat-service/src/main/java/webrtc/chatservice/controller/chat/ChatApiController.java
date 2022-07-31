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
