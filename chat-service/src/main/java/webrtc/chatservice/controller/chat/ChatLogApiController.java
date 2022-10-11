package webrtc.chatservice.controller.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webrtc.chatservice.domain.ChatLog;
import webrtc.chatservice.dto.chat.FindChatLogsResponse;
import webrtc.chatservice.service.chat.ChatLogService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webrtc/chat")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChatLogApiController {

    private final ChatLogService chatLogService;

    @GetMapping("/channel/{channelId}/{idx}")
    public ResponseEntity<FindChatLogsResponse> findChatLogs(@PathVariable("channelId") String channelId, @PathVariable("idx") String idx) {
        List<ChatLog> chatLogs = chatLogService.findChatLogsByIndex(channelId, Long.parseLong(idx));
        return new ResponseEntity<>(new FindChatLogsResponse(chatLogs), HttpStatus.OK);
    }
}
