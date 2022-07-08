package webrtc.chatservice.controller.channel;

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

    @GetMapping("/channel/{id}/{idx}")
    public ResponseEntity<FindChatLogsResponse> findChatLogs(@PathVariable("id") String channelId, @PathVariable("idx") String idx) {
        FindChatLogsResponse response = new FindChatLogsResponse(chatService.findChatLogsByIndex(channelId, Long.parseLong(idx)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
